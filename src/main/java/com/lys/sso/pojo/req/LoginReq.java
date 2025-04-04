package com.lys.sso.pojo.req;

import lombok.Data;

/**
 * 登录请求
 *
 * @author Lys
 * @date 2025/4/3
 */
@Data
public class LoginReq {

  /**
   * 用户名
   */
    private String username;

    /**
     * 密码
     */
    private String password;
}
