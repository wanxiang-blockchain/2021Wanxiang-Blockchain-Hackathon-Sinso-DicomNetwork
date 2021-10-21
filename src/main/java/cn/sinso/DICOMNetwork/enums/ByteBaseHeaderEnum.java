package cn.sinso.DICOMNetwork.enums;

import lombok.Getter;

/**
 * @author lee
 * @date 2021-04-16
 */
@Getter
public enum ByteBaseHeaderEnum {
    // 请求头
    X_AMZ_DATE("x-amz-date"),
    X_AMZ_SECURITY_TOKEN("x-amz-security-token"),
    X_AMZ_CONTENT_SHA256("x-amz-content-sha256"),
    ;

    private String name;

    ByteBaseHeaderEnum(String name) {
        this.name = name;
    }
}
