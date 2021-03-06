package cn.sinso.DICOMNetwork.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AWSV4Auth {

    private AWSV4Auth() {}

    public static class Builder {

        private String accessKeyID;
        private String secretAccessKey;
        private String regionName;
        private String serviceName;
        private String httpMethodName;
        private String canonicalURI;
        private TreeMap<String, String> queryParametes;
        private TreeMap<String, String> awsHeaders;
        private String payload;

        public Builder(String accessKeyID, String secretAccessKey) {
            this.accessKeyID = accessKeyID;
            this.secretAccessKey = secretAccessKey;
        }

        public Builder regionName(String regionName) {
            this.regionName = regionName;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder httpMethodName(String httpMethodName) {
            this.httpMethodName = httpMethodName;
            return this;
        }

        public Builder canonicalURI(String canonicalURI) {
            this.canonicalURI = canonicalURI;
            return this;
        }

        public Builder queryParametes(TreeMap<String, String> queryParametes) {
            this.queryParametes = queryParametes;
            return this;
        }

        public Builder awsHeaders(TreeMap<String, String> awsHeaders) {
            this.awsHeaders = awsHeaders;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public AWSV4Auth build() {
            return new AWSV4Auth(this);
        }
    }

    private String accessKeyID;
    private String secretAccessKey;
    private String regionName;
    private String serviceName;
    private String httpMethodName;
    private String canonicalURI;
    private TreeMap<String, String> queryParametes;
    private TreeMap<String, String> awsHeaders;
    private String payload;

    /* Other variables */
    private final String HMACAlgorithm = "AWS4-HMAC-SHA256";
    private final String aws4Request = "aws4_request";
    private String strSignedHeader;
    private String xAmzDate;
    private String currentDate;

    private AWSV4Auth(Builder builder) {
        accessKeyID = builder.accessKeyID;
        secretAccessKey = builder.secretAccessKey;
        regionName = builder.regionName;
        serviceName = builder.serviceName;
        httpMethodName = builder.httpMethodName;
        canonicalURI = builder.canonicalURI;
        queryParametes = builder.queryParametes;
        awsHeaders = builder.awsHeaders;
        payload = builder.payload;

        //Get current timestamp value.(UTC)
        xAmzDate = getTimeStamp();
        currentDate = getDate();
    }

    /**
     * ?????? 1????????????????????? 4 ??????????????????
     *
     * @return
     */
    private String prepareCanonicalRequest() {
        StringBuilder canonicalURL = new StringBuilder("");

        //Step 1.1 HTTP?????? GET, PUT, POST???DELETE
        canonicalURL.append(httpMethodName).append("\n");

        //Step 1.2 URI
        canonicalURI = canonicalURI == null || canonicalURI.trim().isEmpty() ? "/" : canonicalURI;
        canonicalURL.append(uriEncode(canonicalURI, false)).append("\n");

        ///* Step 1.3 ??????????????????
        StringBuilder queryString = new StringBuilder("");
        if (queryParametes != null && !queryParametes.isEmpty()) {
            for (Map.Entry<String, String> entrySet : queryParametes.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                queryString.append(key).append("=").append(uriEncode(value, false)).append("&");
            }

            queryString.deleteCharAt(queryString.lastIndexOf("&"));
            queryString.append("\n");
            canonicalURL.append(queryString);
        } else {
            queryString.append("\n");
            canonicalURL.append("\n");
        }

        // Step 1.4 ??????headers, ??????header???????????????
        StringBuilder signedHeaders = new StringBuilder("");
        if (awsHeaders != null && !awsHeaders.isEmpty()) {
            for (Map.Entry<String, String> entrySet : awsHeaders.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                signedHeaders.append(key).append(";");
                canonicalURL.append(key).append(":").append(value.trim()).append("\n");
            }
            canonicalURL.append("\n");
        } else {
            canonicalURL.append("\n");
        }

        //Step 1.5 ???????????????headers
        strSignedHeader = signedHeaders.substring(0, signedHeaders.length() - 1); // ??????????????? ";"
        canonicalURL.append(strSignedHeader).append("\n");

        /* Step 1.6 ???HTTP???HTTPS???body??????SHA256??????. */
        payload = payload == null ? "" : payload;
//        canonicalURL.append(generateHex(payload));
        canonicalURL.append((payload));

        System.out.println("##Canonical Request:\n" + canonicalURL.toString());
        return canonicalURL.toString();
    }

    /**
     * ?????? 2????????????????????? 4 ??????????????????
     * stringToSign
     *
     * @param canonicalURL
     * @return
     */
    private String prepareStringToSign(String canonicalURL) {
        String stringToSign = "";

        /* Step 2.1 ?????????????????????????????????. */
        stringToSign = HMACAlgorithm + "\n";

        /* Step 2.2 ????????????????????????. */
        stringToSign += xAmzDate + "\n";

        /* Step 2.3 ??????????????????????????????. */
        stringToSign += currentDate + "/" + regionName + "/" + serviceName + "/" + aws4Request + "\n";

        /* Step 2.4 ????????????1???????????????URL?????????????????????????????????. */
        stringToSign += generateHex(canonicalURL);

        System.out.println("##String to sign:\n" + stringToSign);

        return stringToSign;
    }

    /**
     * ?????? 3?????? AWS Signature ?????? 4 ????????????
     * Signatrue
     *
     * @param stringToSign
     * @return
     */
    private String calculateSignature(String stringToSign) {
        try {
            /* Step 3.1 ???????????????key */
            byte[] signatureKey = getSignatureKey(secretAccessKey, currentDate, regionName, serviceName);

            /* Step 3.2 ????????????. */
            byte[] signature = HmacSHA256(signatureKey, stringToSign);

            /* Step 3.2.1 ????????????????????? */
            String strHexSignature = bytesToHex(signature);
            return strHexSignature;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * ?????? 4??????????????????????????????????????????headers
     *
     * @return
     */
    public Map<String, String> getHeaders() {
        awsHeaders.put("x-amz-date", xAmzDate);
        awsHeaders.put("x-amz-content-sha256", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");

        /* ???????????? 1: ??????aws v4??????????????????????????????. */
        String canonicalURL = prepareCanonicalRequest();

        /* ???????????? 2: ?????????????????????????????? 4. */
        String stringToSign = prepareStringToSign(canonicalURL);

        /* ???????????? 3: ????????????. */
        String signature = calculateSignature(stringToSign);

        if (signature != null) {
            Map<String, String> header = new HashMap<String, String>(0);

            header.put("Authorization", buildAuthorizationString(signature));
//            header.put("x-amz-content-sha256", generateHex(payload));
            header.put("x-amz-content-sha256", payload);
            header.put("x-amz-date", xAmzDate);

            System.out.println("##Signature:\n" + signature);
            System.out.println("##Header:");
            for (Map.Entry<String, String> entrySet : header.entrySet()) {
                System.out.println(entrySet.getKey() + ":" + entrySet.getValue());
            }
            System.out.println("================================");

            return header;
        } else {

            System.out.println("##Signature:\n" + signature);

            return null;
        }
    }

    /**
     * ???????????????????????????????????????Authorization header???.
     *
     * @param strSignature
     * @return
     */
    private String buildAuthorizationString(String strSignature) {
        return HMACAlgorithm + " "
                + "Credential=" + accessKeyID + "/" + getDate() + "/" + regionName + "/" + serviceName + "/" + aws4Request + ", "
                + "SignedHeaders=" + strSignedHeader + ", "
                + "Signature=" + strSignature;
    }

    /**
     * ????????????16?????????.
     * Hex(SHA256Hash(<payload>)
     *
     * @param data
     * @return
     */
    private String generateHex(String data) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes("UTF-8"));
            byte[] digest = messageDigest.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ????????????key??????HmacSHA256??????????????????.
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     * @reference: http://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html#signature-v4-examples-java
     */
    private byte[] HmacSHA256(byte[] key, String data) throws Exception {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
    }

    /**
     * ??????AWS ????????????
     *
     * @param key
     * @param date
     * @param regionName
     * @param serviceName
     * @return
     * @throws Exception
     * @reference http://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html#signature-v4-examples-java
     */
    private byte[] getSignatureKey(String key, String date, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
        byte[] kDate = HmacSHA256(kSecret, date);
        byte[] kRegion = HmacSHA256(kDate, regionName);
        byte[] kService = HmacSHA256(kRegion, serviceName);
        byte[] kSigning = HmacSHA256(kService, aws4Request);
        return kSigning;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * ????????????????????????16???????????????
     *
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }

    /**
     * ??????yyyyMMdd'T'HHmmss'Z'?????????????????????
     *
     * @return
     */
    private String getTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//server timezone
        return dateFormat.format(new Date());
    }

    /**
     * ??????yyyyMMdd?????????????????????
     *
     * @return
     */
    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//server timezone
        return dateFormat.format(new Date());
    }

    /**
     * @param input
     * @param encodeSlash
     * @return
     */
    private String uriEncode(CharSequence input, boolean encodeSlash) {
        //???????????????????????????????????????encodeParameter????????????????????????
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-' || ch == '~' || ch == '.') {
                result.append(ch);
            } else if (ch == '/') {
                result.append(encodeSlash ? "%2F" : ch);
            } else {
                result.append(byteToHexUTF8(ch));
            }
        }
        return result.toString();
    }

//    public static byte[] charToByteArray(char c){
//        byte[] b = new byte[2];
//        b[0] = (byte) ((c & 0xFF00) >> 8);
//        b[1] = (byte) (c & 0xFF);
//        return b;
//    }

    private String byteToHexUTF8(char c) {
        Charset charset = Charset.forName("utf-8");
        CharBuffer charBuffer = CharBuffer.allocate(1);
        charBuffer.put(c);
        charBuffer.flip();//???????????????????????????
        ByteBuffer byteBuffer = charset.encode(charBuffer);
        byte[] bytes = byteBuffer.array();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append("%").append(Integer.toHexString(bytes[i] & 0xFF).toUpperCase());
        }
        return stringBuffer.toString();
    }
}