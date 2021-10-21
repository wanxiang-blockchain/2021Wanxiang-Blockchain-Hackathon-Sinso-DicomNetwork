package cn.sinso.DICOMNetwork.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @author lee
 * @date 2021-04-16
 */
public class FileSha256Util {

    /**
     * 获取文件哈希
     *
     * @param file 文件
     * @return 文件哈希
     */
    public static String getFileSha256(File file) {
        String str = "";
        try {
            str = getHash(file, "SHA-256");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private static String getHash(File file, String hashType) throws Exception {
        InputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        for (int numRead = 0; (numRead = fis.read(buffer)) > 0; ) {
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return toHexString(md5.digest());
    }

//    private static String toHexString(byte[] b) {
//        StringBuilder sb = new StringBuilder();
//        for (byte aB : b) {
//            sb.append(Integer.toHexString(aB & 0xFF));
//        }
//        return sb.toString();
//    }

    private static String toHexString(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

}
