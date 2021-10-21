package cn.sinso.DICOMNetwork.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 静态处理
 *
 * @author sum
 * @version 1.0.0
 * @date 2021/3/27
 */
@Component
public class FinalUtil {

    FinalUtil(){
        PropertiesUtils.initMap();
    }
    private static Integer mapCounts = 10;

    @Value("${mapCounts}")
    public void setUrl(Integer mapCounts) {
        FinalUtil.mapCounts = mapCounts;
    }


    public static final Map<String, String> studyIdmaps;

    static {
        studyIdmaps = new LinkedHashMap<String, String>() {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<String, String> pEldest) {
                return size() > mapCounts;
            }
        };
    }

    public static void addMap(String key, String value) {
        studyIdmaps.put(key, value);
        PropertiesUtils.writeMap();
    }

    public static String getMap(String key) {
        return studyIdmaps.get(key);
    }




}
