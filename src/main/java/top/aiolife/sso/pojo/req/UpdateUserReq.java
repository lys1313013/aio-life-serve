package top.aiolife.sso.pojo.req;

import lombok.Data;

/**
 * 更新用户信息请求参数
 *
 * @author Lys
 * @date 2026/3/1
 */
@Data
public class UpdateUserReq {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 头像
     */
    private String avatar;
}
