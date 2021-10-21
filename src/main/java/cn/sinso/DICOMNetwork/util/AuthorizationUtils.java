package cn.sinso.DICOMNetwork.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class AuthorizationUtils {
    public static void main(String args[]) throws Exception {
        String code = getMachineCode().toUpperCase();
        System.out.println("机器码：" + code);
    }

    public static String getMachineCode() {
        Set<String> result = new HashSet<>();
        String mac = getMac();
        System.out.println("mac:" + getMac());
        result.add(mac);
        Properties props = System.getProperties();
        String javaVersion = props.getProperty("java.version");
        result.add(javaVersion);
        // System.out.println("Java的运行环境版本：    " + javaVersion);
        String javaVMVersion = props.getProperty("java.vm.version");
        result.add(javaVMVersion);
        // System.out.println("Java的虚拟机实现版本：    " +
        // props.getProperty("java.vm.version"));
        String osVersion = props.getProperty("os.version");
        result.add(osVersion);
        // System.out.println("操作系统的版本：    " + props.getProperty("os.version"));
        String code = MD5Util.encodeMD5Hex(result.toString());
        return getSplitString(code, "-", 4);

    }
    private static String getSplitString(String str, String split, int length) {
        int len = str.length();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i % length == 0 && i > 0) {
                temp.append(split);
            }
            temp.append(str.charAt(i));
        }
        String[] attrs = temp.toString().split(split);
        StringBuilder finalMachineCode = new StringBuilder();
        for (String attr : attrs) {
            if (attr.length() == length) {
                finalMachineCode.append(attr).append(split);
            }
        }
        String result = finalMachineCode.toString().substring(0,
                finalMachineCode.toString().length() - 1);
        return result;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String getMac() {
    if(1==1){return "5E-9C-D2-DC-54-BE";}
        try {
            Enumeration<NetworkInterface> el = NetworkInterface
                    .getNetworkInterfaces();
            while (el.hasMoreElements()) {
                byte[] mac = el.nextElement().getHardwareAddress();
                if (mac == null)
                { continue;}
                String hexstr = bytesToHexString(mac);
                String s = getSplitString(hexstr, "-", 2).toUpperCase();
                System.out.println("mac:" +s);
                return s;

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static String getKey(String filePath){
        try {
            File tempFile = new File(filePath);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(tempFile));
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String h = "";
            String out = "";
            String str = "";
            while ((str = bf.readLine()) != null) {
                out += str;
            }
            String[] split = out.split("a=\"#&#");
            h = split[1];
            String[] split1 = h.split("\";return");
            bf.close();
            inputReader.close();
            String decode = "#&#" + split1[0];
            String s1 = decode.replaceAll("#&#", "\\\\u").replaceAll("##", "003");
//            System.out.println(s1);
            String s = decodeUnicode(s1);
            return s;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "";
    }
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }



}
