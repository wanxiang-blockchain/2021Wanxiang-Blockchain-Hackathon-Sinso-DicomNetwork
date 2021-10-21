package cn.sinso.DICOMNetwork.enums;

import lombok.Getter;

/**
 * 冰塔api响应码
 *
 * @author lee
 * @date 2021-03-20
 */
@Getter
public enum BytebaseCodeEnum {
    // 冰塔api响应码描述
    SIGNATURE_FAIL(-400208, "签名验证失败"),
    PARAM_VERIFY_FAIL(-400300, "参数校验失败"),
    SUCCESS(200, "成功"),
    USER_NOT_EXIST(-400203, "用户不存在"),
    SERVER_ERROR(-500100, "服务器内部发生代码执行错误"),
    FILE_UPLOAD_ERROR(-400250, "文件上传失败, 获取上传文件发生错误"),
    FILE_SIZE_RANGE(-400251, "长传文件超过系统设定的最大值,系统允许的最大值（M）"),
    FILE_TYPE_NOT_SUPPORT(-400252, "文件 mime 类型不允许"),
    ;
    private Integer code;
    private String msg;

    BytebaseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsgByCode(Integer code) {
        for (BytebaseCodeEnum value : BytebaseCodeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getMsg();
            }
        }
        return null;
    }
}
