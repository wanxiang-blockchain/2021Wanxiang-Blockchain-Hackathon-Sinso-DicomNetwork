package cn.sinso.DICOMNetwork.model;

import lombok.Data;

/**
 * @author lee
 * @date 2021-03-20
 */
@Data
public class FileInfo {
    private String dealCid;

    /**
     * 接收 deal 的矿工
     */
    private String dealMiner;

    /**
     * 文件唯一标识
     */
    private String fileCid;

    /**
     * 标识 deal 唯一的 id
     */
    private String proposalCid;

    /**
     * 消息
     */
    private String message;

    /**
     * 接收方
     */
    private String provider;

    /**
     * 订单 id
     */
    private Integer dealId;

    /**
     * 发送方
     */
    private String client;

    /**
     * 文件大小
     */
    private Integer pieceSize;

    /**
     * 验证结果
     */
    private Boolean verifiedDeal;

    /**
     * deal 结束高度
     */
    private Integer endDeal;

    /**
     * deal 起始高度
     */
    private Integer startDeal;

    /**
     * 价格
     */
    private Integer price;

    /**
     * 矿工的抵押
     */
    private Integer minerCollateral;

    /**
     * 订单状态
     */
    private Integer state;

    /**
     * 订单状态描述
     */
    private String stateName;
}
