package cn.sinso.DICOMNetwork.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version V4.0
 * @Title: ConvertUtil
 * @Company: 成都影达科技有限公司
 * @Description: 描述
 * @author: Lee
 * @date : 29/12/18 17:43
 */
public final class ConvertUtil {

    private static final Logger log = LoggerFactory.getLogger(ConvertUtil.class);

    /**
     * 原对象转换为新的对象
     *
     * @param source      原对象
     * @param targetClazz 新对象的类型
     */
    public static <E, T> T sourceToTarget(E source, Class<T> targetClazz) {
        if (null == source) {
            return null;
        }
        Assert.notNull(targetClazz.getName(), "转换类型不能为空");
        T target = BeanUtils.instantiateClass(targetClazz);
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 原对象集合转换为新的对象集合
     *
     * @param sources     原对象集合
     * @param targetClazz 新对象集合的对象类型
     */
    public static <E, T> List<T> sourcesToTargets(List<E> sources, Class<T> targetClazz) {
        Assert.notNull(targetClazz.getName(), "转换类型不能为空");
        if (null != sources && !sources.isEmpty()) {
            Class<? extends List> sourcesClazz = sources.getClass();
            List<T> targets = BeanUtils.instantiateClass(sourcesClazz);
            for (E e : sources) {
                T target = BeanUtils.instantiateClass(targetClazz);
                BeanUtils.copyProperties(e, target);
                targets.add(target);
            }
            return targets;
        } else {
            return null;
        }
    }

    /**
     * 字符串数组转换为数组对象
     *
     * @param strings 字符串数组
     * @return [{"label":"","value":""}] label的值和value的值相同
     */
    public static List<Map<String, String>> stringsToListHashMap(List<String> strings) {
        if (null == strings) {
            return null;
        }

        List<Map<String, String>> result = new ArrayList<>(strings.size());
        for (String str : strings) {
            Map<String, String> map = new HashMap<>(2);
            map.put("label", str);
            map.put("value", str);
            result.add(map);
        }
        return result;
    }




    /**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map装换为javabean对象
     *
     * @param map
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    /**
     * 将List<T>转换为List<Map<String, Object>>
     *
     * @param objList
     * @return
     */

    public static <T> List<Map<String, Object>> objectsToMaps(List<T> objList) {
        List<Map<String, Object>> list = new ArrayList();
        if (objList != null && objList.size() > 0) {
            Map<String, Object> map = null;
            T bean = null;
            for (int i = 0, size = objList.size(); i < size; i++) {
                bean = objList.get(i);
                map = beanToMap(bean);
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 将List<Map<String,Object>>转换为List<T>
     *
     * @param maps
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList();
        if (maps != null && maps.size() > 0) {
            Map<String, Object> map = null;
            T bean = null;
            for (int i = 0, size = maps.size(); i < size; i++) {
                map = maps.get(i);
                bean = clazz.newInstance();
                mapToBean(map, bean);
                list.add(bean);
            }
        }
        return list;
    }


    public static String dcmReplaceAll(String str, String path, String url) {
        if(str==null || path==null || url==null) {return str;}
        str = str.replaceAll("\\\\", "/");
        path = path.replaceAll("\\\\", "/");
        str=str.replaceAll(path,url);
        return str;

    }


    public static Integer parseInt(String num){
       Integer out=0;
        try {
            out = Integer.parseInt(num);
        }catch (Exception e){

        }
        return out;
    }

    public static float parseFloat(String num){
        float out=0.0f;
        try {
           out = Float.parseFloat(num);
        }catch (Exception e){

        }
        return out;
    }

    public static double parseDouble(String num){
        double out=0.0;
        try {
            out = Double.parseDouble(num);
        }catch (Exception e){

        }
        return out;
    }
}
