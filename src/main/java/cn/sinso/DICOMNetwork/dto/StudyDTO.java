package cn.sinso.DICOMNetwork.dto;


import cn.sinso.DICOMNetwork.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @version V4.0
 * @Title: StudyDTO
 * @Company: 成都影达科技有限公司
 * @Description: 列表数据封装
 * @author: Lee
 * @date : 19-1-2 下午2:28
 */
@Setter
@Getter
public class StudyDTO implements Serializable {
    private static final long serialVersionUID = -5772881537798623566L;
    private static final Logger log = LoggerFactory.getLogger(StudyDTO.class);

    private Long id;

    /**
     * 病人号码
     */
    private String patientNum;

    private String accessionNum;

    private Date birthDate;

    private String birthDateView;
    private BigDecimal cost;
    /**
     * 姓名
     */
    private String name;

    private String phone;

    private String hosPhone;

    /**
     * 年龄
     */
    private String age;

    private String ageType;

    private String ageView;

    /**
     * 性别
     */
    private String gender;

    /**
     * 检查部位
     */
    private String pointName;

    /**
     * 检查类型（如：CT等）
     */
    private String classifyType;

    /**
     * 来源：门诊，住院等
     */
    private String source;

    /**
     * 检查状态
     */
    private Integer status;

    private String bedNum;

    private Date examTime;

    private Date printTime;

    private String printTimeView;

    private String examTimeView;

    private String summary;

    private String finding;

    private String address;

    private String hosAddress;

    /**
     * 检查状态
     */
    private String statusView;

    /**
     * 报告id
     */
    private Long reportId;

    /**
     * 报告打印次数
     */
    private Integer printNum;

    /**
     * 报告医师
     */
    private String reportUsername;

    @JsonIgnore
    private String reportUsernameVal;

    private String reportUsernameSign;

    /**
     * 图像数
     */
    private Integer picNum;

    /**
     * 报告时间
     */
    @JsonIgnore
    private Date reportTime;

    /**
     * 报告时间(前端展示)
     */
    private String reportTimeView;

    /**
     * 登记时间
     */
    @JsonIgnore
    private Date insertTime;

    /**
     * 登记时间
     */
    private String insertTimeView;

    /**
     * 申请科室
     */
    private String applyDeptName;

    /**
     * 申请医生
     */
    private String applyUsername;

    private String docPhone;

    /**
     * 检查科室
     */
    private String deptName;

    /**
     * 临床诊断
     */
    private String clinicalDiagnosis;

    /**
     * 检查号
     */
    private String studyNum;

    /**
     * 诊断结果
     */
    private String impression;

    /**
     * 胶片打印次数
     */
    private Integer filmPrintNum;

    private String operateUsername;

    /**
     * 急诊标识（1非急诊  2急诊）
     */
    @JsonIgnore
    private Integer emergency;

    /**
     * 随访标识（1无随访 2随访）
     */
    @JsonIgnore
    private Integer flup;

    /**
     * 急诊和随访前端展示参数
     */
    private List<Integer> emergencyAndFlup;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 审核医师
     */
    private String verifyUsername;

    private String verifyUsernameVal;

    private String verifyUsernameSign;

    /**
     * 审核时间
     */
    @JsonIgnore
    private Date verifyTime;

    /**
     * 审核时间
     */
    private String verifyTimeView;

    /**
     * 驳回日期
     */
    @JsonIgnore
    private Date rejectTime;

    /**
     * 驳回日期
     */
    private String rejectTimeView;

    /**
     * 驳回医师
     */
    private String rejectUsername;

    /**
     * 驳回原因
     */
    private String rejectDetail;

    private String hosCode;

    /**
     * 医院名称
     */
    private String hosName;

    private String platCode;

    private String assignUsername;

    /**
     * 阴阳性
     */
    @JsonIgnore
    private Integer isPositive;

    /**
     * 阴阳性
     */
    private String isPositiveView;

    /**
     * 门诊号
     */
    private String outpatientNum;

    /**
     * 住院号
     */
    private String admissionNum;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 图标展示
     */
//    @JsonIgnore
    private String iconDisplay;

    /**
     * 会诊标识
     */
    private Integer consultationValue;

    /**
     * 图标展示
     */
    private List<Map<String, String>> iconsDisplay;

    private String deptType;

    private String placeOfBirth;
//    private String address;
    private String race;
    private String profession;




    public String getAge(){
        if(age==null || "".equals(age)){
            this.age="0,0,0,0";
        }
        String[] split = age.split(",");
        if(split.length!=4){
            this.age= split[0]+",0,0,0";
        }
        return this.age;
    }

    public String getAgeType(){
        if(ageType==null || "".equals(ageType)){
            //默认岁
            this.ageType="1";
        }
        return this.ageType;
    }

    /**
     * 年龄展示
     */
    public String getAgeView() {
        String view="";
        String age = getAge();
        String ageType = getAgeType();

        String[] typeSplit = ageType.split(",");
        String[] ageSplit = age.split(",");

        for (String type:typeSplit) {
            int iType = Integer.parseInt(type);
            String sType="";
            switch (iType){
                case 1: sType="岁"; break;
                case 2: sType="月"; break;
                case 3: sType="周"; break;
                case 4: sType="日"; break;
            }
            view+=ageSplit[iType-1]+sType;
        }

        return view;
    }
    /**
     * 急诊和随访前端展示转化
     */
    public List<Integer> getEmergencyAndFlup() {
        if (null != this.emergency && null != this.flup) {
            List<Integer> emergencyAndFlup = new ArrayList<>(2);
            emergencyAndFlup.add(this.emergency);
            emergencyAndFlup.add(this.flup);
//            if(this.platCode!=null && !"".equals(this.platCode)){
//                emergencyAndFlup.add(2);
//            }else{
//                //不加
//            }
            this.emergencyAndFlup = emergencyAndFlup;
        }
        return emergencyAndFlup;
    }

    /**
     * 阴阳性转换
     */
    public String getIsPositiveView() {
        if (this.isPositive!=null && this.isPositive==2) {
            this.isPositiveView = "阳性";
        } else  {
            this.isPositiveView = "阴性";
        }
        return this.isPositiveView;
    }

    public String getGender() {
        if ("F".equals(this.gender)) {
            this.gender ="女";
        } else if ("M".equals(this.gender)) {
            this.gender = "男";
        }
        return this.gender;
    }



    /**
     * 报告时间转换
     *
     * @return
     */
    public String getReportTimeView() {
        if (null != this.reportTime) {
            this.reportTimeView = DateUtil.formatYMDHM(this.reportTime);
        }
        return this.reportTimeView;
    }

    /**
     * 登记时间转换
     *
     * @return
     */
    public String getInsertTimeView() {
        if (null != this.insertTime) {
            this.insertTimeView = DateUtil.formatYMDHM(this.insertTime);
        }
        return this.insertTimeView;
    }

    public String getPrintTimeView() {
        if (null != this.printTime) {
            this.printTimeView = DateUtil.formatYMDHMS(this.printTime);
        }else{
            this.printTimeView = DateUtil.formatYMDHMS(new Date());
        }
        return this.printTimeView;
    }

    public String getBirthDateView() {
        if (null != this.birthDate) {
            this.birthDateView = DateUtil.formatYMD(this.birthDate);
        }
        return this.birthDateView;
    }
    /**
     * 审核时间转换
     *
     * @return
     */
    public String getVerifyTimeView() {
        if (null != this.verifyTime) {
            this.verifyTimeView = DateUtil.formatYMDHM(this.verifyTime);
        }
        return this.verifyTimeView;
    }

    /**
     * 驳回日期转换
     *
     * @return
     */
    public String getRejectTimeView() {
        if (null != this.rejectTime) {
            this.rejectTimeView = DateUtil.formatYMDHM(this.rejectTime);
        }
        return this.rejectTimeView;
    }

    /**
     * 检查时间转换
     */
    public String getExamTimeView() {
        if (null != examTime) {
            this.examTimeView = DateUtil.formatYMDHM(examTime);
        }
        return this.examTimeView;
    }

    public String getIconDisplay() {
        if ((null == iconDisplay || "".equals(iconDisplay)) && id !=null) {
           return this.id.toString();
        }
        return this.iconDisplay;
    }
    public String getIconDisplayTrue(){
        return this.iconDisplay;
    }



}
