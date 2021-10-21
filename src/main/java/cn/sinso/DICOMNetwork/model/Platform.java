package cn.sinso.DICOMNetwork.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhoucong
 * @date 2021-02-22
 */
@Data
public class Platform implements Serializable {

    private Integer id;

    private String platformCode;

    private String platformName;

    private String reportInterface;

    private String dicomInterface;

    private String imageInterface;

    private Integer status;

    private String remark;

    private static final long serialVersionUID = 1L;
}