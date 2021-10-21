package cn.sinso.DICOMNetwork.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UploadDTO implements Serializable {
    private String remark;
    private String fileName;
    private String cid;
}
