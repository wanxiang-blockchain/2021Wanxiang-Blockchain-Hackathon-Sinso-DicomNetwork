package cn.sinso.DICOMNetwork.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;


/**
 * MD5组件
 */
public class MD5Util {
    private static final Logger log = LoggerFactory.getLogger(MD5Util.class);
    /**
     * MD5加密
     *
     * @param data
     *            待加密数据
     * @return byte[] 消息摘要
     *
     * @throws Exception
     */
    public static byte[] encodeMD5(String data) throws Exception {

        // 执行消息摘要
        return DigestUtils.md5(data);
    }

    /**
     * MD5加密
     *
     * @param data
     *            待加密数据
     * @return byte[] 消息摘要
     *
     * @throws Exception
     */
    public static String encodeMD5Hex(String data) {
        // 执行消息摘要
        return DigestUtils.md5Hex(data);
    }

    public static String getMD5File(String filePath) {
        // 执行消息摘要
        FileInputStream fileInputStream=null;
        try {
            fileInputStream= new FileInputStream(filePath);
            String md5=DigestUtils.md5Hex(fileInputStream);
            return md5;
        }catch (Exception ex){
            return null;
        }finally {
            try {
                fileInputStream.close();
            }catch (Exception ex){

            }
        }

    }
//
//    /**
//     * 解密
//     * @param data
//     * @return
//     * @throws Exception
//     */
//    public String decode(String data) throws Exception {
//        Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        deCipher.init(Cipher.DECRYPT_MODE, key, iv);
//        BASE64Decoder base64Decoder = new BASE64Decoder();
//        //此处注意doFinal()的参数的位数必须是8的倍数，否则会报错（通过encode加密的字符串读出来都是8的倍数位，但写入文件再读出来，就可能因为读取的方式的问题，导致最后此处的doFinal()的参数的位数不是8的倍数）
//        //此处必须用base64Decoder，若用data。getBytes()则获取的字符串的byte数组的个数极可能不是8的倍数，而且不与上面的BASE64Encoder对应（即使解密不报错也不会得到正确结果）
//        byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));
//        return new String(pasByte, this.charset);
//    }
//
//    public static void main(String[] args) {
//        // 25d55ad283aa400af464c76d713c07ad
//        String ps = encodeMD5Hex("12345678");
//        log.info("25d55ad283aa400af464c76d713c07ad\n",ps);
//    }

}
