package cn.sinso.DICOMNetwork.dto;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class SendDTO implements Serializable {

    private List<Long> fileIds;

    private List<Long> docIds;

    private Date expireTime;
}
