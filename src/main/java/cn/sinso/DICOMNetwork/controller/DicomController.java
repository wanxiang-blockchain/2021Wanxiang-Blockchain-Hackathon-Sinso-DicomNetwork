package cn.sinso.DICOMNetwork.controller;


import cn.sinso.DICOMNetwork.dto.ResultDTO;
import cn.sinso.DICOMNetwork.enums.ResponseCodeEnum;
import cn.sinso.DICOMNetwork.model.DicomInfo;
import cn.sinso.DICOMNetwork.model.StudyInfo;
import cn.sinso.DICOMNetwork.util.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Dicom浏览器接口
 *
 * @author zhoucong
 * @date 2021-02-22
 */
@RestController
@RequestMapping("/api/dicom")
@Slf4j
public class DicomController {

    @Value("${verifyCode}")
    private String verifyCode;

    @Value("${ipfs_url}")
    private String ipfs_url;

    @GetMapping("/getByStudyId")
    public ResultDTO getDicomInfoByStudyId(HttpServletRequest request, String studyId, String opt) {
        studyId = (studyId == null ? "" : studyId);
        if ((studyId == null || "".equals(studyId))) {
            return ResultDTO.build(ResponseCodeEnum.PARAMETER_ERROR);
        }


        ResultDTO resultDTO = new ResultDTO();
        if (opt != null && "detail".equals(opt)) {
            // 处理获取详情
            List<StudyInfo> studyInfoList = new ArrayList<>();
            return ResultDTO.ok(studyInfoList);
        } else {
            Map<String, Object> dicomInfoMap = new HashMap<>();
            //上传IPFS的
            if (studyId.startsWith("c")) {
                dicomInfoMap = getArraySqlStudyId(studyId.replace("c", ""));
            }
            //web3store的
            else if(studyId.startsWith("d")){
                if(studyId.contains("#")){
                    String[] split = studyId.split("#");
                    if(split.length<2){
                        return ResultDTO.build(ResponseCodeEnum.PARAMETER_ERROR);
                    }
                    if(split[1].equals("")){
                        return ResultDTO.build(ResponseCodeEnum.PARAMETER_ERROR);
                    }
                    Date expire_time = DateUtil.formatYMDHMS(split[2]);
                    if(new Date().after(expire_time)){
                        return ResultDTO.build(ResponseCodeEnum.TOKEN_EXPIRE);
                    }

                    dicomInfoMap = getArraySqlStudyIdWeb3(split[0].replace("d", ""));
                }else{
                    dicomInfoMap = getArraySqlStudyIdWeb3(studyId.replace("d", ""));
                }


            }
            //不存数据库，存内存的
            else{
                dicomInfoMap = getArrayStudyIdIn(studyId);

            }

            resultDTO.setStatus(200);
            resultDTO.setData(dicomInfoMap);
            // 返回授权码（原setMsg）
            resultDTO.setMsg("");

        }
        return resultDTO;
    }


    public Map<String, Object> getArrayStudyIdIn(String studyId) {
        String urls = FinalUtil.getMap(studyId);
        ArrayList dicoms = new ArrayList();
        if (urls != null && !"".equals(urls)) {
            dicoms = new ArrayList(Arrays.asList(urls.split(",")));
        }
        Map<String, Object> outMap = new HashMap<>();
        outMap.put("data", dicoms);
        outMap.put("seriesCount", -1);
        outMap.put("fileType", "zip");
        return outMap;
    }

    public Map<String, Object> getArraySqlStudyId(String studyId) {
        ArrayList dicoms = new ArrayList();
        String sql= "select * from dicom_list where id="+studyId;
        List<Map<String, Object>> urls = SqlUtil.getBySql(sql);
        for (Map<String, Object> url : urls) {
            dicoms.add(ipfs_url+url.get("file_cid"));
        }

        Map<String, Object> outMap = new HashMap<>();
        outMap.put("data", dicoms);
        outMap.put("seriesCount", 1);
        outMap.put("fileType", "zip");
        return outMap;
    }

    public Map<String, Object> getArraySqlStudyIdWeb3(String studyId) {
        ArrayList dicoms = new ArrayList();
        String sql= "select * from file_list where id in("+studyId+")";
        List<Map<String, Object>> urls = SqlUtil.getBySql(sql);
        for (Map<String, Object> url : urls) {
            dicoms.add(url.get("link_url"));
        }

        Map<String, Object> outMap = new HashMap<>();
        outMap.put("data", dicoms);
        outMap.put("seriesCount", 1);
        outMap.put("fileType", "zip");
        return outMap;
    }


    @GetMapping("/getList")
    public ResultDTO getList(HttpServletRequest request) {
        String sql= "select * from dicom_list";
        List<Map<String, Object>> urls = SqlUtil.getBySql(sql);
        for (Map<String, Object> url : urls) {
            String studyId="c"+url.get("id").toString();
            String tokenStr = BtoaEncode.botaEncodePassword(studyId);
            url.put("studyId",tokenStr);
        }
        return ResultDTO.ok(urls);


    }


    @PostMapping("/add")
    public ResultDTO add(HttpServletRequest request, @RequestBody Map<String,Object> map) {
        String cid = map.get("cid").toString();
        String size = map.get("size").toString();
        String sql= "insert into dicom_list(file_cid,file_size,status) values('"+cid+"',"+size+",1)";
        SqlUtil.updateBySql(sql);
        return ResultDTO.ok();


    }









    public Map<String, Object> getDicomInfoByStudyIdIn(String studyId) {

        String urls = FinalUtil.getMap(studyId);
        ArrayList dicoms = new ArrayList(Arrays.asList(urls.split(",")));


        Map<String, Map<String, List<DicomInfo>>> examDateMap = new HashMap<>();
        Map<String, List<DicomInfo>> seriesMap = new HashMap<>();
        List<DicomInfo> studyList = new ArrayList<>();
        String study_id = studyId;
        String exam_date = "1";
        String series_id = "1";

        Map<String, Map<String, Map<String, List<DicomInfo>>>> studyMap = new HashMap<>();
        for (int i = 0; i < dicoms.size(); i++) {
            DicomInfo dicomInfo = new DicomInfo();
            dicomInfo.setUrl(dicoms.get(i).toString());
            dicomInfo.setImageType("dicom");
            dicomInfo.setSeriesId("1");
            dicomInfo.setId((long) i);
            studyList.add(dicomInfo);
        }
        seriesMap.put(series_id, studyList);
        examDateMap.put(exam_date, seriesMap);
        studyMap.put(study_id, examDateMap);
        for (Map.Entry<String, Map<String, Map<String, List<DicomInfo>>>> entryStudyid : studyMap.entrySet()) {
            //studyid层
//            String mapKey = entry.getKey();
            Map<String, Map<String, List<DicomInfo>>> studyidMapValue = entryStudyid.getValue();
            //examDate层
            for (Map.Entry<String, Map<String, List<DicomInfo>>> entryExamDate : studyidMapValue.entrySet()) {

                Map<String, List<DicomInfo>> newExamMapValue = new HashMap<>();
                Map<String, Date> sortMap = new HashMap<>();
                //seriesId层
                Map<String, List<DicomInfo>> examMapValue = entryExamDate.getValue();
                //排序
                for (Map.Entry<String, List<DicomInfo>> entrySeries : examMapValue.entrySet()) {
                    List<DicomInfo> seriesValue = entrySeries.getValue();
                    Date examDate = seriesValue.get(0).getExamDate();
                    sortMap.put(entrySeries.getKey(), examDate);
                }
                Map<String, Date> map = sortByValue(sortMap, false);
                int count = 1;
                for (Map.Entry<String, Date> entrySeries : map.entrySet()) {
                    newExamMapValue.put(count + "", examMapValue.get(entrySeries.getKey()));
                    count++;
                }
                examMapValue.clear();
                examMapValue.putAll(newExamMapValue);

            }

        }

        Map<String, Object> outMap = new HashMap<>();
        outMap.put("data", studyMap);
        outMap.put("seriesCount", 1);
        return outMap;
    }

    public static Map sortByValue(Map map, final boolean reverse) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (reverse) {
                    return -((Comparable) ((Map.Entry) (o1)).getValue())
                            .compareTo(((Map.Entry) (o2)).getValue());
                }
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


}
