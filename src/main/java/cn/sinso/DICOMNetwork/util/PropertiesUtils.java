//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.sinso.DICOMNetwork.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

@Component
public class PropertiesUtils {


    public static String driverClassName;
    public static String db_url;
    public static String db_username;
    public static String db_password;

    @Value("${driverClassName}")
    public void setDriverClassName(String driverClassName) {
        PropertiesUtils.driverClassName = driverClassName;
    }

    @Value("${db_url}")
    public void setDb_url(String db_url) {
        PropertiesUtils.db_url = db_url;
    }

    @Value("${db_username}")
    public void setDb_username(String db_username) {
        PropertiesUtils.db_username = db_username;
    }

    @Value("${db_password}")
    public void setDb_password(String db_password) {
        PropertiesUtils.db_password = db_password;
    }

//    public PropertiesUtils() {
//    }


    public static String getValue(String key) {
        if ("driverClassName".equals(key)) {
            return driverClassName;
        }
        if ("db_url".equals(key)) {
            return db_url;
        }
        if ("db_username".equals(key)) {
            return db_username;
        }
        if ("db_password".equals(key)) {
            return db_password;
        }
        return "";

    }


    public static void initMap() {
        String property = System.getProperty("user.dir");
        String mapPath = property + File.separator + "scanMapHash.txt";
        String str = FileUtil.readFile(mapPath);
        JSONObject jsonObject = JSONObject.parseObject(str);
        System.out.print("读取scanMapHash.txt");
        //json对象转Map
        Map<String, Object> map = (Map<String, Object>) jsonObject;
        if (map != null) {
            System.out.print("初始化写入scanMap");

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                FinalUtil.studyIdmaps.put(entry.getKey(), entry.getValue().toString());
            }
        }
    }


    public static void writeMap() {
        String property = System.getProperty("user.dir");
        String mapPath = property + File.separator + "scanMapHash.txt";
        FileUtil.writeFile(mapPath, JSON.toJSONString(FinalUtil.studyIdmaps));

    }
}
