package cn.sinso.DICOMNetwork.dto;



import cn.sinso.DICOMNetwork.enums.ResponseCodeEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * @version V3.0
 * @Title: ResultDTO
 * @Company:
 * @Description: 描述
 * @author: alibeibei
 * @date 2018/11/13 下午4:37
 */
public class ResultDTO<T> implements Serializable {

    private static final long serialVersionUID = 7393143705508563753L;
    // 响应时间戳
    private Date timestamp;

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private T data;

    public static ResultDTO ok(Object data) {
        return new ResultDTO(data);
    }

    public static ResultDTO ok() {
        return new ResultDTO(null);
    }

    public ResultDTO() {

    }

    public static ResultDTO build(Integer status, String msg, Object data) {
        return new ResultDTO(status, msg, data);
    }

    public static ResultDTO build(Integer status, String msg) {
        return new ResultDTO(status, msg, null);
    }

    public static ResultDTO build(ResponseCodeEnum codeEnum) {
        return new ResultDTO(codeEnum.getCode(), codeEnum.getMessage(), null);
    }

    public ResultDTO(Integer status, String msg, T data) {
        this.timestamp = new Date(System.currentTimeMillis());
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public ResultDTO(T data) {
        this.timestamp = new Date(System.currentTimeMillis());
        this.status = ResponseCodeEnum.SUCCESS.getCode();
        this.msg = ResponseCodeEnum.SUCCESS.getMessage();
        this.data = data;
    }

    public boolean checkOk() {
        return this.getStatus() == ResponseCodeEnum.SUCCESS.getCode();
    }

    public boolean checkErr() {
        return !checkOk();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
