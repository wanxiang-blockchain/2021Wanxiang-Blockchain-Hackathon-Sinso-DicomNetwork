package cn.sinso.DICOMNetwork.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Component
public class AWSV4AuthUtil {



    /**
     * 如果没有传入其它的key,则表示使用admin账户进行操作
     * @param uri
     * @param method
     * @param querys
     * @param body
     * @param accessKeyID
     * @param secretAccessKey
     * @return
     */
    public  Map<String, String> getHeaders(String uri, String method, TreeMap<String, String> querys,
                                          String body, String accessKeyID, String secretAccessKey,String host,String location) {
        TreeMap<String, String> awsHeaders = new TreeMap();
        awsHeaders.put("host",host);
        AWSV4Auth.Builder builder;
        builder = new AWSV4Auth.Builder(accessKeyID, secretAccessKey);
//        return builder.regionName("us-east-1")
        return builder.regionName(location)
                .serviceName("s3")
                .httpMethodName(method)
                .canonicalURI(uri)
                .queryParametes(querys)
                .awsHeaders(awsHeaders)
                .payload(body)
                .build()
                .getHeaders();
    }

//    /**
//     * caps: user read
//     * @param uid
//     * @param accessKeyID
//     * @param secretAccessKey
//     * @return
//     */
//    //查询某个用户
//    public FindOneUserR findOneUser(String uid, String accessKeyID, String secretAccessKey) {
//        String uri = "/admin/user";
//        TreeMap<String, String> queyrs = new TreeMap();
//        queyrs.put("uid", uid);
//
//        Map<String, String> header = getHeaders(uri, "GET", queyrs, null, accessKeyID, secretAccessKey,"");
//        String entityString;
//        try {
//            HttpResponse httpResponse = HttpUtils.doGet("http://" + s3Configure.getHostname(), uri, header, queyrs);
//            if (200 != httpResponse.getStatusLine().getStatusCode()) {
//                return null;
//            }
//            HttpEntity httpEntity = httpResponse.getEntity();
//            entityString = EntityUtils.toString(httpEntity,"utf-8");
//        } catch (Exception e) {
//            throw new MyException("解析某个用户错误！");
//        }
//        FindOneUserR findUserR = JsonUtil.toMyObject(entityString, FindOneUserR.class);
//        return findUserR;
//    }
}