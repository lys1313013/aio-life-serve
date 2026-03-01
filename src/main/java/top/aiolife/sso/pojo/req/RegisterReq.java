package top.aiolife.sso.pojo.req;

import lombok.Data;

/**
 * 注册请求参数
 *
 * @author Lys
 * @date 2026/03/01
 */
@Data
public class RegisterReq {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;
}
