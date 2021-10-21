package cn.sinso.DICOMNetwork.dto;


import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class TokenUser implements Serializable {
    private static final long serialVersionUID = 9076028038207075837L;
    /**
     * token信息
     */
    private String token;

    /**
     * 姓名
     */
    private String name;

    /**
     * 电话号码
     */
    private Long userId;

    /**
     * 用户执业医院
     */
    private String email;


    public TokenUser() {

    }

    public TokenUser(String token,
                     String email,
                     String name,
                     Long userId
                    ) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.userId = userId;
    }


}

