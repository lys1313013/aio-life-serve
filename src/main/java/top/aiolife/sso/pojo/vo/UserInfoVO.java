package top.aiolife.sso.pojo.vo;

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
    private Long id;

    private String realName;

    private String username;

    private String nickname;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 邮件
     */
    private String email;
    /**
     * 个人简介
     */
    private String introduction;
    private List<String> roles;
}
