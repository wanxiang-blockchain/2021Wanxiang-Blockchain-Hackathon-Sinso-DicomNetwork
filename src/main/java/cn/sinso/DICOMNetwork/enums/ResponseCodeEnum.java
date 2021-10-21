package cn.sinso.DICOMNetwork.enums;

/**
 * @version V3.0
 * @Title: ResponseCodeEnum
 * @Company: 成都影达科技有限公司
 * @Description: 描述
 * @author: 东进
 * @date 2018/11/28 上午10:11
 */
public enum ResponseCodeEnum {
    // 系统通用
    SUCCESS(200, "操作成功"),
    NOT_HAVE_DATA(200, "查无数据"),
    SYSTEM_IS_NOT_ENABLED(300, "系统尚未授权，请联系管理员"),
    SYSTEM_IS_EXPIRED(301, "系统授权过期，请联系管理员"),

    IS_NULL(400, "id不能为空"),
    UNAUTHORIZED(401, "认证失败"),
    OPERATE_FORBIDDEN(402, "您不是管理员，无权操作"),
    USER_NOT_EXSIST(403, "用户不存在"),
    FORBIDDEN(404, "权限不足，无法访问~"),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED(405, "请求方式错误"),
    OPERATE_FAIL(500, "操作失败"),
    UNKNOWN_FAIL(501, "未知异常"),
    DELETE_FAIL(502, "删除失败"),
    NETWORK_EXCEPTION(503, "网络异常"),
    TIMEOUT_EXCEPTION(504, "连接超时"),
    SERIALIZATION_EXCEPTION(505, "序列化错误"),
    UPLOAD_FILE_IS_NULL(506, "上传文件为空"),

    // 用户
    GET_USER_AUTH_INFO_FAILED(2004, "根据条件获取用户授权信息失败"),
    NOT_PLAT_ADMIN(2005, "您不是平台管理员，无权操作"),
    NOT_VERIRY_DOC(2006, "您不是审核医师，无权操作"),
    NOT_REPORT_DOC(2007, "您不是报告医师，无权操作"),
    QUALITY_CONTROL_PROHIBITED(2008, "质控验证信息不合法"),


    // 登录
    LOGIN_SUCCESS(200, "登录成功"),
    LOGIN_FAILURE(2103, "登录失败"),
    USERNAME_PASSWORD_UNAUTHORIZED(2101, "用户名不存在或者密码错误"),
    USER_IS_NOT_ENABLED(2102, "账号被禁用，请联系管理员"),
    PASSWORD_UNAUTHORIZED(2108, "原密码错误"),
    USER_IS_LOCKED(2107, "账号被锁定，请联系管理员"),
    NOT_LOGIN_IN(2104, "用户未登录，请登录"),
    LOGIN_INVALID(2105, "登录失效,请重新登录"),
    NO_HOS_SELECTED(2106, "用户未选择医院"),
    LOGIN_ERROR(2109, "登录信息错误，请重新登录"),
    TOKEN_ERROR(2100, "token验证失败，请重新登录"),




    TOKEN_EXPIRE(220, "token过期，请重新发邮件获取"),
    TOKEN_EMPTY(221, "token为空，请发邮件获取"),
    TOKEN_FAILED(222, "token验证失败，请重新发邮件获取"),





    // 访问限流
    LIMIT_ACCESS_TOO_FREQUENTLY(2201, "访问过于频繁，请慢一点"),
    LIMIT_ACCESS_EXCEEDS_MAXIMUN(2202, "访问人数过多，请稍后再试"),
    PARAMETER_ERROR(10000, "参数异常"),
    INSERT_FAIL(10001, "插入失败"),
    REPEAT_FAIL(10002, "数据重复"),
    HOS_CODE_CANT_BE_BLANK(10004, "医院编码不能为空或者空串"),
    PARAMETER_IS_NOT_EMPTY(10003, "参数不能为空或者零"),

    //report
    REPORT_NOT_EXIST(30001, "检查单不存在"),
    REPORT_OCCUPY(30002, "报告占用中"),

    // study
    PATIENT_NUM_ERROR(30003, "手工登记病人号码或者检查号码不符合规范，请重新获取"),
    STUDY_NUM_ERROR(30004, "检查号码有误，请重新获取"),
    CONSULTATION_IS_GOING(30005,"会诊正在进行中，不能操作"),
    CANNOT_SEND_CONSULTATION(30006,"会诊只能在待诊断状态时发起"),

    //follow_up
    FOLLOW_UP_IS_COMPLETE(30011, "随访已完成，不能再修改");

    private Integer code;
    private String message;

    ResponseCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public final Integer getCode() {
        return this.code;
    }

    public final String getMessage() {
        return this.message;
    }
}
