package cn.sinso.DICOMNetwork.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhoucong
 * @date 2021-02-22
 */
@Data
public class Appconfig implements Serializable {

    private Integer id;

    private String code;

    private String value;

    private String remark;

    private static final long serialVersionUID = 1L;
}
