package cn.sinso.DICOMNetwork.controller;

import cn.sinso.DICOMNetwork.ByteBaseClient;
import cn.sinso.DICOMNetwork.dto.ResultDTO;
import cn.sinso.DICOMNetwork.dto.SendDTO;
import cn.sinso.DICOMNetwork.dto.TokenUser;
import cn.sinso.DICOMNetwork.dto.UploadDTO;
import cn.sinso.DICOMNetwork.enums.ResponseCodeEnum;
import cn.sinso.DICOMNetwork.model.MailClient;
import cn.sinso.DICOMNetwork.util.*;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/api/email")
@Slf4j
public class InterfaceController {
    @Value("${tmpPath}")
    private String tmpPath;
     @Value("${tokenExpireTime}")
    private Integer tokenExpireTime;


    @Autowired
    MailClient mailClient;
    @Autowired
    private ByteBaseClient byteBaseClient;

    @GetMapping("/getDocList")
    public ResultDTO getDocList(String docName,Integer level,Integer dept, HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        ResultDTO resultDTO = getUserId(accessToken);
        if(resultDTO.getStatus()!=200){
            return resultDTO;
        }
        Long userId= (Long)resultDTO.getData();


        String whereStr="";
        if(docName!=null && !"".equals(docName) ){
            whereStr+=" and doc_name like '%"+docName+"%'";
        } if(level!=null && level!=0){
            whereStr+=" and doc_level ="+level;
        }if(dept!=null && dept!=0){
            whereStr+=" and dept ="+dept;
        }

        String sqlDoc= "select id,doc_name as docName,doc_email as docEmail,doc_level as docLevel,biref_info as birefInfo,dept " +
                "from doc_list where 1=1 "+whereStr;
        List<Map<String, Object>> docs = SqlUtil.getBySql(sqlDoc);
        if(docs==null || docs.size()==0){

        }
        return ResultDTO.ok(docs);


    }

    @GetMapping("/getToken")
    public ResultDTO getToken(String email) {

        String sqlUser= "select * from user_list where user_email ='"+email+"'";
        List<Map<String, Object>> users = SqlUtil.getBySql(sqlUser);
        if(users==null || users.size()==0){
            String sqlInsert= "insert into user_list(user_email,name,remark) values('"+email+"','"+""+"','"+""+"')";
            SqlUtil.updateBySql(sqlInsert);
            users = SqlUtil.getBySql(sqlUser);
        }
        Map<String, Object> userMap = users.get(0);
        Long userId = (Long)userMap.get("id");


        // 生成uuid-token
        String token = UUID.randomUUID().toString().replace("-", "");
        Date diffDate = DateUtil.getDiffDate(tokenExpireTime, Calendar.DATE);
        String dateStr = DateUtil.formatYMDHMS(diffDate);

//        TokenUser tokenUser= new TokenUser(token,email,"",user_id;
//        String tokenStr = JSON.toJSONString(tokenUser);

        String sqlDelet= "delete from token_list where user_id ="+userId;
        SqlUtil.updateBySql(sqlDelet);
        String sql= "insert into token_list(token,expire_time,user_id) values('"+token+"','"+dateStr+"','"+userId+"')";
        SqlUtil.updateBySql(sql);


        String interfaceUrl = mailClient.getInterfaceUrl();
        String content = EmailUtil.buildContent(email, interfaceUrl+"?token=" + token, diffDate.toString(),
                tokenExpireTime.toString());
       try {
           EmailUtil.sendEmail(mailClient, new ArrayList<String>(Arrays.asList(email)), "Log in to sinso.dicom-network", content, false);
       }catch (Exception e){
           System.out.println(e.getMessage());
       }
        return ResultDTO.ok(token);

    }

    @GetMapping("/getFileList")
    public ResultDTO getFileList(String fileName,String startTime,String endTime,HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        ResultDTO resultDTO = getUserId(accessToken);
        if(resultDTO.getStatus()!=200){
            return resultDTO;
        }
        String whereStr="";
        if(fileName!=null && !"".equals(fileName) ){
            whereStr+=" and file_name like '%"+fileName+"%'";
        } if(startTime!=null && endTime!=null &&!"".equals(startTime)&& !"".equals(endTime)){
            whereStr+=" and create_time between '"+startTime+"' and '"+endTime+"'";
        }

        Long userId= (Long)resultDTO.getData();
        String sqlFile= "select id,user_id as userId,file_name as fileName,cid,link_url as linkUrl,create_time as createTime,remark " +
                        "from file_list where user_id ="+userId+whereStr;
        List<Map<String, Object>> files = SqlUtil.getBySql(sqlFile);
        if(files==null || files.size()==0){

        }
        return ResultDTO.ok(files);

    }

    @PostMapping("/send")
    public ResultDTO send(@RequestBody SendDTO sendDTO, HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        ResultDTO resultDTO = getUserId(accessToken);
        if(resultDTO.getStatus()!=200){
            return resultDTO;
        }
        Long userId= (Long)resultDTO.getData();
        List<Long> docIds = sendDTO.getDocIds();
        List<Long> fileIds = sendDTO.getFileIds();
        Date expireTime = sendDTO.getExpireTime();

        String fileIdStr = StringUtils.join(fileIds, ",");
        String docIdStr = StringUtils.join(docIds, ",");

        String s ="d"+fileIdStr+"#"+docIdStr+"#"+DateUtil.formatYMDHMS(expireTime);

        String interfaceUrl = mailClient.getInterfaceUrl();
        s=BtoaEncode.botaEncodePassword(s);
        String url=interfaceUrl.replace("8080","8078")+ "api/Interface/GetImageViewerNet?token="+s;

        return ResultDTO.ok(url);

    }

    @PostMapping("/upload")
    public ResultDTO upload(@RequestBody UploadDTO uploadDTO,
                            HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        ResultDTO resultDTO = getUserId(accessToken);
        if(resultDTO.getStatus()!=200){
            return resultDTO;
        }
        Long userId= (Long)resultDTO.getData();
        String cid = uploadDTO.getCid();
        String fileName = uploadDTO.getFileName();
        String remark = uploadDTO.getRemark();

        String linkUrl="https://"+cid+".ipfs.dweb.link/"+fileName;

        String sql= "insert into file_list(user_id,file_name,cid,link_url,create_time,remark) values("
                +userId+",'"+ fileName+"','"+cid+"','"+linkUrl+"','"+DateUtil.formatYMDHMS(new Date())
                +"','"+remark+"')";
        SqlUtil.updateBySql(sql);
        return ResultDTO.ok();

    }
    @PostMapping("/uploadBT")
    public ResultDTO uploadBT(String remark, HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        ResultDTO resultDTO = getUserId(accessToken);
        if(resultDTO.getStatus()!=200){
            return resultDTO;
        }
        Long userId= (Long)resultDTO.getData();

        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Iterator iter = multiRequest.getFileNames();
        MultipartFile file = null;
        String path = "";
        try {
            file = multiRequest.getFile(iter.next().toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        if (file != null) {
            path = tmpPath + file.getOriginalFilename();
            try {
                //暂存临时目录
                file.transferTo(new File(path));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            return ResultDTO.build(ResponseCodeEnum.UPLOAD_FILE_IS_NULL);
        }
        String fileId="";
        try {
            fileId = byteBaseClient.upload(path);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        String linkUrl="";

        String sql= "insert into file_list(user_id,file_name,cid,link_url,create_time,remark) values("
                +userId+",'"+ file.getOriginalFilename()+"','"+fileId+"','"+linkUrl+"','"+DateUtil.formatYMDHMS(new Date())
                +"','"+remark+"')";
        SqlUtil.updateBySql(sql);
        return ResultDTO.ok(fileId);

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
