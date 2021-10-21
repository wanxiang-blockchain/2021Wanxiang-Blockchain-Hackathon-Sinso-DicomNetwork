package cn.sinso.DICOMNetwork.model;

import lombok.Data;

/**
 * @author lee
 * @date 2021-03-20
 */
@Data
public class RetrieveInfo {
    /**
     * 0-未检索 1-检索中 2-检索成功 3-检索失败
     */
    private Integer state;

    /**
     * 检索id
     */
    private String retrieveId;

    /**
     * 下载的url
     */
    private String downloadUrl;

    /**
     * 检索实时价格
     */
    private String retrieveAmount;
}
