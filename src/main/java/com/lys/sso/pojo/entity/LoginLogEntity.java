package com.lys.sso.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 登录日志
 *
 * @author Lys
 * @date 2025/06/22 20:45
 */
@Data
@TableName("login_log")
public class LoginLogEntity {
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 创建时间
     */
    private Timestamp createdAt;
    /**
     * ip地址
     */
    private String ipAddress;
}
