package cn.sinso.DICOMNetwork.model;

import lombok.Data;

/**
 * @author lee
 * @date 2021-03-20
 */
@Data
public class BytebaseResp {
    /**
     * 响应码
     * -400208 签名验证失败
     * -400300 参数校验失败
     * 200 成功
     * -400203 用户不存在
     * -500100 服务器内部发生代码执行错误
     * -400250 文件上传失败, 获取上传文件发生错误
     * -400251 长传文件超过系统设定的最大值,系统允许的最大值（M）
     * -400252 文件 mime 类型不允许
     */
    private Integer code;

    /**
     * 数据
     */
    private String data;

    /**
     * 描述
     */
    private String msg;
}
