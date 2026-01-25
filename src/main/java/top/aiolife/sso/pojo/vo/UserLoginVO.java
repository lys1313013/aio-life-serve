package top.aiolife.sso.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/04 17:48
 */
@Getter
@Setter
public class UserLoginVO {
    private Integer id;
    private String realName;
    private List<String> roles;
    private String username;
    private String accessToken;
}
