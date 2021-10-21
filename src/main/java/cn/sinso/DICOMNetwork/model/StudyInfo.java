package cn.sinso.DICOMNetwork.model;



import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhoucong
 * @date 2021-02-22
 */
@Data
public class StudyInfo implements Serializable {

    private Long id;

    //会诊ID
    private String consulId;

    private String patientNum;

    private String studyNum;

    private String classifyType;

    private Date examTime;

    private String gender;

    private String age;

    private Date insertTime;

    private String pointName;

    private String itemName;

    private Integer status;

    private static final long serialVersionUID = 1L;
}
