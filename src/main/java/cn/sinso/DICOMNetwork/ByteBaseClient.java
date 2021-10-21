package cn.sinso.DICOMNetwork;


import cn.sinso.DICOMNetwork.enums.ByteBaseHeaderEnum;
import cn.sinso.DICOMNetwork.enums.BytebaseCodeEnum;
import cn.sinso.DICOMNetwork.enums.ResCodeEnum;
import cn.sinso.DICOMNetwork.exception.ByteBaseException;
import cn.sinso.DICOMNetwork.model.BytebaseResp;
import cn.sinso.DICOMNetwork.model.FileInfo;
import cn.sinso.DICOMNetwork.model.RetrieveInfo;
import cn.sinso.DICOMNetwork.util.AWSV4AuthUtil;
import cn.sinso.DICOMNetwork.util.DateUtil;
import cn.sinso.DICOMNetwork.util.DateUtils;
import cn.sinso.DICOMNetwork.util.FileSha256Util;
import cn.iinda.xhttputils.HttpClient;
import com.alibaba.fastjson.JSON;

import com.google.gson.annotations.JsonAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 冰塔数据API
 *
 * @author lee
 * @date 2021-03-20
 */
@Slf4j
@Data
public class ByteBaseClient {
    @NotNull
    private String accessKeyId;
    @NotNull
    private String secretAccessKey;
    /**
     * 区域
     */
    @NotNull
    private String location;
    /**
     * 服务类型
     */
    @NotNull
    private String serviceType;
    /**
     * 冰塔Api域名
     */
    @NotNull
    private String host;

    @NotNull
    private String bucket;


    public ByteBaseClient() {
    }

    public ByteBaseClient(String accessKeyId,
                          String secretAccessKey,
                          String location,
                          String serviceType,
                          String host,
                          String bucket) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.location = location;
        this.serviceType = serviceType;
        this.host = host;
        this.bucket = bucket;
    }

    private static final String SIGN_HEADERS = "host;x-amz-content-sha256;x-amz-date";

    /**
     * 获取请求头
     *
     * @param sessionToken sessionToken
     * @param file         文件
     * @return 请求头
     */
    private Map<String, String> generateHeaders(String sessionToken, File file) {
        Map<String, String> headers = new HashMap<>(5);
        // 1.请求头中添加当前时间
        headers.put(ByteBaseHeaderEnum.X_AMZ_DATE.getName(), LocalDateTime.now().toString());
        // 2.如果sessionToken不为空就添加到请求头中
        if (StringUtils.isNotBlank(sessionToken)) {
            headers.put(ByteBaseHeaderEnum.X_AMZ_SECURITY_TOKEN.getName(), sessionToken);
        }
        // 3.从请求头中取得 X-Amz-Content-Sha256,为空则赋值 UNSIGNED-PAYLOAD
        // 4.根据服务器类型 (ServiceTypeS3 = "s3" | ServiceTypeSTS = "sts"),如果是标准型服务器为ServiceTypeSTS 则删除请求头 X-Amz-Content-Sha256
        if (!"sts".equals(serviceType)) {
            String contentSha256 = FileSha256Util.getFileSha256(file);
            if (StringUtils.isNotBlank(contentSha256)) {
                headers.put(ByteBaseHeaderEnum.X_AMZ_CONTENT_SHA256.getName(), contentSha256);
            } else {
                headers.put(ByteBaseHeaderEnum.X_AMZ_CONTENT_SHA256.getName(), "UNSIGNED-PAYLOAD");
            }
        }
        return headers;
    }



    private String GetAuthorization(String method ,String canonicalURI,String canonicalQueryString ,String date,
                                    String amzDate,String amzContentSha256){
//        amzDate="20210827T050051Z";
        String signedHeaders = "host;x-amz-content-sha256;x-amz-date";
        String credential = accessKeyId + "/" + date + "/" + location + "/" + serviceType + "/aws4_request";
        String CanonicalHeaders = "host:" + host + "\n" + "x-amz-content-sha256:" + amzContentSha256 + "\n" + "x-amz" + "-date:" + amzDate + "\n";
        String canonicalRequest = method + "\n" + canonicalURI + "\n" + canonicalQueryString + "\n" + CanonicalHeaders + "\n" + signedHeaders + "\n" + amzContentSha256;
        String stringToSign = "AWS4-HMAC-SHA256" + "\n" + amzDate + "\n" + date + "/" + location + "/" + serviceType + "/aws4_request" + "\n" + DigestUtils.sha256Hex(canonicalRequest);
//        String signingKey = hmacSha256(hmacSha256(hmacSha256(hmacSha256(("AWS4"+secretAccessKey).getBytes(), date.getBytes()).getBytes(), (location).getBytes()).getBytes(),(serviceType).getBytes()).getBytes(), ("aws4_request").getBytes());
//        String signingKey = hmacSha256(hmacSha256(hmacSha256(hmacSha256(("AWS4"+secretAccessKey), date), location),serviceType), "aws4_request");
//        String signature = Hex.encodeHexString(hmacSha256(signingKey.getBytes(), stringToSign.getBytes()).getBytes());
//        String signature = Hex.encodeHexString(hmacSha256(signingKey, stringToSign).getBytes());

        byte[] signingKey = HmacSHA256(HmacSHA256(HmacSHA256(HmacSHA256(("AWS4"+secretAccessKey).getBytes(), date.getBytes()), (location).getBytes()),(serviceType).getBytes()), ("aws4_request").getBytes());
        byte[] bytesSign = HmacSHA256(signingKey, stringToSign.getBytes());
        String signature = Hex.encodeHexString(bytesSign);

        return "AWS4-HMAC-SHA256 " + "Credential=" + credential + "," + "SignedHeaders=" + signedHeaders + "," + "Signature=" + signature;
    }

    /**
     * 获取加密auth
     *
     * @param httpMethod  请求类型
     * @param uri         路径
     * @param query       参数
     * @param date        日期
     * @param amzDate     请求时间
     * @param payloadHash 内容哈希
     * @return 加密auth
     */


    private String getAuthorization(String httpMethod, String uri, String query, String date, String amzDate, String payloadHash) {
        String headers = "host:" + host + "\n" +
                ByteBaseHeaderEnum.X_AMZ_CONTENT_SHA256 + ":" + payloadHash + "\n" +
                ByteBaseHeaderEnum.X_AMZ_DATE + ":" + amzDate + "\n";
        // 签名规范
        String signRequest = httpMethod + "\n" +
                uri + "\n" +
                query + "\n" +
                headers + "\n" +
                ByteBaseHeaderEnum.X_AMZ_DATE.getName() + ":" + amzDate + "\n" +
                SIGN_HEADERS + "\n" +
                payloadHash;
        String signRequestHash = DigestUtils.sha256Hex(signRequest);
        // 签名摘要
        String signStr = "AWS4-HMAC-SHA256" + "\n" +
                amzDate + "\n" +
                date + "/" +
                location + "/" +
                serviceType + "/" +
                "aws4_request" + "\n" +
                signRequestHash;


        byte[] signingKey =
                hmacSha256Byte(hmacSha256Byte(hmacSha256Byte(hmacSha256Byte(("AWS4"+secretAccessKey).getBytes(),
                date.getBytes()), (location).getBytes()),(serviceType).getBytes()), ("aws4_request").getBytes());
        String sign = Hex.encodeHexString(hmacSha256Byte(signingKey,signStr.getBytes()));


//        // 签名key
//        String key1 = hmacSha256(("AWS4" + secretAccessKey).getBytes(), amzDate.split("T")[0].getBytes());
//        if (key1 == null) {
//            throw new ByteBaseException(-10000, "签名错误");
//        }
//        String key2 = hmacSha256(key1.getBytes(), location.getBytes());
//        if (key2 == null) {
//            throw new ByteBaseException(-10000, "签名错误");
//        }
//        String key3 = hmacSha256(key2.getBytes(), serviceType.getBytes());
//        if (key3 == null) {
//            throw new ByteBaseException(-10000, "签名错误");
//        }
//        String signKey = hmacSha256(key3.getBytes(), "aws4_request".getBytes());
//        if (signKey == null) {
//            throw new ByteBaseException(-10000, "签名错误");
//        }
//        // hmacSha256生成摘要
//        String s = hmacSha256(signKey.getBytes(), signStr.getBytes());
//        if (s == null) {
//            throw new ByteBaseException(-10000, "签名错误");
//        }

        // 签名

//        String sign = Hex.encodeHexString(s.getBytes());
        System.out.println("sign:"+sign);
        // 证书
        String credential = accessKeyId + "/" + date + "/" + location + "/" + serviceType + "/aws4_request";
        // 拼接加密auth
        return "AWS4-HMAC-SHA256 Credential=" + credential + ",SignedHeaders=" + SIGN_HEADERS + ",Signature=" + sign;
    }

    /**
     * 上传本地文件
     *
     * @param filePath
     * @return
     */
    public String upload(String filePath) {
        // 添加文件
        List<File> files = new ArrayList<>();
        File file = new File(filePath);
        files.add(file);
        String[] split = filePath.split("/");
        String fileName = split[split.length - 1];
        Map<String, List<File>> filesMap = new HashMap<>();
        filesMap.put("files", files);

        // 构建所需参数
        Date now = new Date();
        Date diffDate = DateUtil.getDiffDate(-8, Calendar.HOUR);
        String date = DateUtils.convertByDate(diffDate, DateUtils.Format.YYYYMMDD);
        String time = DateUtils.convertByDate(diffDate, DateUtils.Format.HHMMSS);
        String amzDate = date + "T" + time + "Z";
        String contentSha256 = FileSha256Util.getFileSha256(file);
        String authorization = GetAuthorization(HttpPut.METHOD_NAME, "/" + bucket+ "/"  + fileName, "", date, amzDate
                , contentSha256);
//        String authorization = GetAuthorization(HttpPost.METHOD_NAME, "/" + bucket + "/" +fileName, "", date, amzDate, contentSha256);

        // 构建header
        Map<String, String> headers = generateHeaders(null, file);
        headers.put("Authorization", authorization);
        headers.put(ByteBaseHeaderEnum.X_AMZ_CONTENT_SHA256.getName(), contentSha256);
        headers.put(ByteBaseHeaderEnum.X_AMZ_DATE.getName(), amzDate);
        TreeMap<String, String> queyrs = new TreeMap();
        Map<String, String> headers1 = new AWSV4AuthUtil().getHeaders("/" + bucket+ "/"  + fileName, "PUT", queyrs,
                contentSha256,
                accessKeyId, secretAccessKey
                ,host,location);

        Map<String, String> headers2 = new HashMap<>();
        headers2.put("Authorization", "AWS4-HMAC-SHA256 Credential=6616f350f73849ddad9561b7517d940b/20210827/cn-north-1/s3/aws4_request,SignedHeaders=host;x-amz-content-sha256;x-amz-date,Signature=c64b8279720706d6d38d70116477ebface7ec847aca00430707039e71859cd4a");
        headers2.put(ByteBaseHeaderEnum.X_AMZ_CONTENT_SHA256.getName(), "fff5bb824aeb290cb942d2e62e649a82019a8a7354e6899c8870c6cea2d25af7");
        headers2.put(ByteBaseHeaderEnum.X_AMZ_DATE.getName(), "20210827T090849Z");

        // 上传文件
        // todo 如果文件超过128M，调用分片上传
//        String url = "https://" + host + "/" + accessKeyId + "/" + fileName;
        String url = "https://" + host + "/" + bucket + "/" + fileName;
//        System.out.println(JSON.toJSONString(headers));
//        System.out.println(JSON.toJSONString(headers1));
        String res = uploadFile(url, headers,filePath);
        Map<String, String> map = JSON.parseObject(res, Map.class);
        return map.get("fileId");
    }

    /**
     * 查询文件信息
     *
     * @param fileId 文件id
     * @return FileInfoDto
     */
    public FileInfo findFileInfo(String fileId) {
//        String url = "https://server-cloud.bytebase.cn/cloudApi/v1/findFileInfo";
//        Map<String, String> body = new TreeMap<>();
//        body.put("fileId", fileId);
//        body.put("userId", userId);
//        String authorization = getAuthorization(body);
//        String resp = HttpClient.post(url).addHeader("Authorization", authorization).json(body).execute();
//        BytebaseResp respDto = JSON.parseObject(resp, BytebaseResp.class);
//        if (!respDto.getCode().equals(BytebaseCodeEnum.SUCCESS.getCode())) {
//            String msg = BytebaseCodeEnum.getMsgByCode(respDto.getCode());
//            throw new ByteBaseException(respDto.getCode(), msg);
//        }
//        return JSON.parseObject(respDto.getData(), FileInfo.class);
        return null;
    }

    /**
     * 检索文件
     *
     * @param fileId 文件id
     * @return fileId
     */
    public String clientRetrieve(String fileId) {
//        String url = "https://server-cloud.bytebase.cn/cloudApi/v1/clientRetrieve";
//        Map<String, String> body = new TreeMap<>();
//        body.put("userId", userId);
//        body.put("fileId", fileId);
//        String authorization = getAuthorization(body);
//        String resp = HttpClient.post(url).addHeader("Authorization", authorization).json(body).execute();
//        BytebaseResp respDto = JSON.parseObject(resp, BytebaseResp.class);
//        if (!respDto.getCode().equals(BytebaseCodeEnum.SUCCESS.getCode())) {
//            String msg = BytebaseCodeEnum.getMsgByCode(respDto.getCode());
//            throw new ByteBaseException(respDto.getCode(), msg);
//        }
//        return respDto.getData();
        return null;
    }

    /**
     * 查询检索文件交易
     *
     * @param url           请求地址
     * @param authorization 签名
     * @param retrieveId    检索id
     * @return RetrieveInfoDto
     */
    public RetrieveInfo findRetrieveInfo(String url, String authorization, String retrieveId) {
        String resp = HttpClient.post(url).addHeader("Authorization", authorization).json(Collections.singletonMap("retrieveId", retrieveId)).execute();
        BytebaseResp respDto = JSON.parseObject(resp, BytebaseResp.class);
        if (!respDto.getCode().equals(BytebaseCodeEnum.SUCCESS.getCode())) {
            String msg = BytebaseCodeEnum.getMsgByCode(respDto.getCode());
            throw new ByteBaseException(respDto.getCode(), msg);
        }
        return JSON.parseObject(respDto.getData(), RetrieveInfo.class);
    }


    /**
     * 提交文件
     *
     * @param fileServer 服务器路径
     * @param filesMap   文件
     * @param headers    header
     * @return
     */
    private String postFile(String fileServer, Map<String, List<File>> filesMap, Map<String, String> headers) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost(fileServer);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                postRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("utf-8"));

        for (Map.Entry<String, List<File>> entry : filesMap.entrySet()) {
            for (File file : entry.getValue()) {
                FileBody fileBody = new FileBody(file);
                builder.addPart(entry.getKey(), fileBody);
            }
        }

        postRequest.setEntity(builder.build());
        try {
            response = httpClient.execute(postRequest);
            HttpEntity responseEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
                StringBuilder buffer = new StringBuilder();
                String str;
                while (reader.readLine() != null && (str = reader.readLine()).trim().length() > 0) {
                    buffer.append(str);
                }
                log.error("上传失败：" + buffer.toString());
                throw new ByteBaseException(ResCodeEnum.UPLOAD_FILE_ERROR);
            } else {
                String res = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                BytebaseResp respDto = JSON.parseObject(res, BytebaseResp.class);
                log.info(JSON.toJSONString(respDto));
                return respDto.getData();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private String putFile(String fileServer, Map<String, List<File>> filesMap, Map<String, String> headers) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut putRequest = new HttpPut(fileServer);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                putRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("utf-8"));

        for (Map.Entry<String, List<File>> entry : filesMap.entrySet()) {
            for (File file : entry.getValue()) {
                FileBody fileBody = new FileBody(file);
//                builder.addPart(entry.getKey(), fileBody);
                builder.addBinaryBody(entry.getKey(), file);
            }
        }

        putRequest.setEntity(builder.build());
        try {
            response = httpClient.execute(putRequest);
            HttpEntity responseEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
                StringBuilder buffer = new StringBuilder();
                String str;
                while (reader.readLine() != null && (str = reader.readLine()).trim().length() > 0) {
                    buffer.append(str);
                }
                log.error("上传失败：" + buffer.toString());
                throw new ByteBaseException(ResCodeEnum.UPLOAD_FILE_ERROR);
            } else {
                String res = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                BytebaseResp respDto = JSON.parseObject(res, BytebaseResp.class);
                log.info(JSON.toJSONString(respDto));
                return respDto.getData();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    public static String uploadFile(String fileServer, Map<String, String> headers,String fileName) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
//            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            CloseableHttpResponse response = null;
//            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000).build();
            HttpPut httpPut = new HttpPut(fileServer);
//            httpPut.setConfig(requestConfig);


            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPut.addHeader(entry.getKey(), entry.getValue());
                }
            }
        HttpEntity entity = new FileEntity(new File(fileName));

//            File file = new File(fileName);
//
//            //multipartEntityBuilder.addBinaryBody("file", file,ContentType.create("image/png"),"abc.pdf");
//            //当设置了setSocketTimeout参数后，以下代码上传PDF不能成功，将setSocketTimeout参数去掉后此可以上传成功。上传图片则没有个限制
//            //multipartEntityBuilder.addBinaryBody("file",file,ContentType.create("application/octet-stream"),"abd.pdf");
//            multipartEntityBuilder.addBinaryBody("file",file);
//            //multipartEntityBuilder.addPart("comment", new StringBody("This is comment", ContentType.TEXT_PLAIN));
//            multipartEntityBuilder.addTextBody("comment", "this is comment");
//            HttpEntity httpEntity = multipartEntityBuilder.build();
            httpPut.setEntity(entity);

        try {
            response = httpClient.execute(httpPut);
            HttpEntity responseEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
                StringBuilder buffer = new StringBuilder();
                String str;
                while (reader.readLine() != null && (str = reader.readLine()).trim().length() > 0) {
                    buffer.append(str);
                }
                log.error("上传失败：" + buffer.toString());
                throw new ByteBaseException(ResCodeEnum.UPLOAD_FILE_ERROR);
            } else {
                String res = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                BytebaseResp respDto = JSON.parseObject(res, BytebaseResp.class);
                log.info(JSON.toJSONString(respDto));
                return respDto.getData();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }


    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    private String hmacSha256(byte[] data, byte[] key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            return byte2hex(mac.doFinal(data));
        } catch (NoSuchAlgorithmException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        } catch (InvalidKeyException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    private  byte[] hmacSha256Byte(byte[] data, byte[] key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] bytes = mac.doFinal(data);
            return bytes;
        } catch (NoSuchAlgorithmException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        } catch (InvalidKeyException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private byte[] HmacSHA256(byte[] key, byte[] data) {
        try {
//            String algorithm = "HmacSHA256";
            String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
            byte[] bytes = mac.doFinal(data);
            for (int i = 0; i <bytes.length ; i++) {
                int i1 = 0xff & bytes[i];
                bytes[i]=(byte)i1 ;
            }

            return bytes;
        } catch (NoSuchAlgorithmException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        } catch (InvalidKeyException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return null;
    }
 private String hmacSha256(String data, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            return byte2hex(mac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        } catch (InvalidKeyException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String temp;
        for (int n = 0; b != null && n < b.length; n++) {
            temp = Integer.toHexString(b[n] & 0XFF);
            if (temp.length() == 1) {
                hs.append('0');
            }
            hs.append(temp);
        }
        return hs.toString().toLowerCase();
    }
}
