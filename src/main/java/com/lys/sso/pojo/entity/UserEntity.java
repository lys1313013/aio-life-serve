package com.lys.sso.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/4/3
 */
@Getter
@Setter
@TableName("user")
public class UserEntity {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * leetcode账号
     */
    private String leetcodeAcct;
}
