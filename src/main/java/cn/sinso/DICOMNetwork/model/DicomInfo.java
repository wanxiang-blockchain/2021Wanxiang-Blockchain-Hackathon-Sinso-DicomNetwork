package cn.sinso.DICOMNetwork.model;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhoucong
 * @date 2021-02-22
 */
@Data
public class DicomInfo implements Serializable {

    private Long id;

    private Long studyId;

    private String md5;

    private String url;

    private String imageType;

    private String classifyType;

    private String seriesId;

    private String imgId;

    private Date examDate;

    private Integer sNum;

    private String remark;

    private Integer flag;

    private Integer orderNum;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}

