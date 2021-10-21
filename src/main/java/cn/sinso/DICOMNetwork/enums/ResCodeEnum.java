package cn.sinso.DICOMNetwork.enums;

import lombok.Getter;

/**
 * 公共错误码枚举
 *
 * @author lee
 * @date 2019/11/1
 */
@Getter
public enum ResCodeEnum {

    //枚举
    SYSTEM_ERROR(27000, "系统异常"),
    UPLOAD_FILE_EMPTY(28002, "上传文件为空"),
    UPLOAD_FILE_ERROR(28003, "文件上传失败"),
    ;
    private Integer errorCode;

    private String errorMsg;

    ResCodeEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
