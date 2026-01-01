package com.lys.sso.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/04 17:44
 */
@Getter
@Setter
public class UserInfoVO {
    private Integer id;

    private String realName;

    private String username;

    /**
     * github 用户名
     */
    private String githubUsername;

    /**
     * github token
     */
    private String githubToken;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 邮件
     */
    private String email;

    private List<String> roles;
}
