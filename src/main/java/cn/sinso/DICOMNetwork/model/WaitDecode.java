package cn.sinso.DICOMNetwork.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class WaitDecode implements Serializable {

    private Integer id;

    private String consulId;

    private String dicomUrl;

    private LocalDateTime insertTime;

    private Integer status;

    private static final long serialVersionUID = 1L;
}
