package cn.sinso.DICOMNetwork.controller;


import cn.sinso.DICOMNetwork.dto.ResultDTO;
import cn.sinso.DICOMNetwork.enums.ResponseCodeEnum;
import cn.sinso.DICOMNetwork.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version V3.0
 * @Title: PageController
 * @Company: 成都影达科技有限公司
 * @Description: 描述
 * @author: 周聪
 * @date 2018/3/7 10:33
 */
@Controller
public class PageController {


    @RequestMapping("/")
    public String index() {
        return "forward:/index";
    }

    @RequestMapping("index")
    public String index(@RequestParam(name = "cardID", required = false) String cardID, Model model, HttpServletRequest request) {

        return "index";
    }


    @RequestMapping("api/Interface/GetImageViewerNet")
    public String GetImageViewerNet(String token, String fileId,HttpServletRequest request, Model model) {
        ResultDTO resultDTO = getUserId(token);
        String tokenStr ="";
        if(resultDTO.getStatus()==200){
            String s ="d"+fileId;
             tokenStr = BtoaEncode.botaEncodePassword(s);
        }else {
             tokenStr = (token);
        }



        String dicompage = "../../views/viewer-pc.html?studyId=" + tokenStr;
        String userAgent = request.getHeader("USER-AGENT")==null?"": request.getHeader("USER-AGENT").toLowerCase();
        if (check(userAgent).equals("Phone") || check(userAgent).equals("Table")) {
            dicompage = "../../views/viewer-tab.html?studyId=" + tokenStr;
        }

        model.addAttribute("dicompage", dicompage);

        return "dicomviewer";
    }



    @RequestMapping("api/Interface/GetImageViewer")
    public String GetImageViewer(String urls, HttpServletRequest request, Model model) {
        String studyId = System.currentTimeMillis()+"";
        FinalUtil.addMap(studyId,urls);
        String tokenStr = BtoaEncode.botaEncodePassword(studyId);
        String dicompage = "../../views/viewer-pc.html?studyId=" + tokenStr;
        String userAgent = request.getHeader("USER-AGENT")==null?"": request.getHeader("USER-AGENT").toLowerCase();
        if (check(userAgent).equals("Phone") || check(userAgent).equals("Table")) {
            dicompage = "../../views/viewer-tab.html?studyId=" + tokenStr;
        }

        model.addAttribute("dicompage", dicompage);

        return "dicomviewer";
    }

    //获取访问设备类型
    public String check(String userAgent) {
        String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i|windows (phone|ce)|blackberry"
                + "|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp|laystation portable)|nokia|fennec|htc[-_]"
                + "|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
        String tableReg = "\\b(ipad|tablet|(Nexus 7)|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
        Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
        Pattern tablePat = Pattern.compile(tableReg, Pattern.CASE_INSENSITIVE);
        if (null == userAgent) {
            userAgent = "";
        }
        // 匹配
        Matcher matcherPhone = phonePat.matcher(userAgent);
        Matcher matcherTable = tablePat.matcher(userAgent);

        if (matcherTable.find()) {
            return "Table";
        } else if (matcherPhone.find()) {
            return "Table";
        } else {
            return "PC";
        }
    }

    //获取访问者IP
    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private ResultDTO getUserId(String accessToken){
        if(accessToken==null || "".equals(accessToken)){
            return ResultDTO.build(ResponseCodeEnum.TOKEN_EMPTY);
        }
        String sqlToken= "select * from token_list where token ='"+accessToken+"'";
        List<Map<String, Object>> tokens = SqlUtil.getBySql(sqlToken);
        if(tokens==null || tokens.size()==0){
            return ResultDTO.build(ResponseCodeEnum.TOKEN_FAILED);
        }
        LocalDateTime expire_time1 = (LocalDateTime)tokens.get(0).get("expire_time");
        Date expire_time = Date.from (expire_time1.atZone(ZoneId.systemDefault()).toInstant());
        if(new Date().after(expire_time)){
            return ResultDTO.build(ResponseCodeEnum.TOKEN_EXPIRE);
        }
        Long userId = (Long) tokens.get(0).get("user_id");
        return ResultDTO.ok(userId);
    }


}
