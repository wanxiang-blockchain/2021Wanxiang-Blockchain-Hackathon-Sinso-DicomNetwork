package cn.sinso.DICOMNetwork.exception;

import cn.sinso.DICOMNetwork.enums.ResCodeEnum;
import lombok.Getter;

import java.io.Serializable;

/**
 * 业务异常
 *
 * @author lee
 */
@Getter
public class ByteBaseException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 5339962879722142051L;

    /**
     * 异常编码
     */
    private Integer errCode;

    /**
     * 异常
     */
    private String errMsg;

    public ByteBaseException(Integer errCode, String errMsg) {
        super();
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public ByteBaseException(ResCodeEnum resCodeEnum) {
        super();
        this.errCode = resCodeEnum.getErrorCode();
        this.errMsg = resCodeEnum.getErrorMsg();
    }
}
