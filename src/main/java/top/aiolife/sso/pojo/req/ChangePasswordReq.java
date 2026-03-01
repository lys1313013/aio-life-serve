package top.aiolife.sso.pojo.req;

import lombok.Data;

/**
 * 修改密码请求
 *
 * @author Lys
 * @date 2026/03/01
 */
@Data
public class ChangePasswordReq {

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}
