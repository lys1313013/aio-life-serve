package top.aiolife.sso.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户列表展示 VO
 *
 * @author Lys
 * @date 2026/03/08
 */
@Getter
@Setter
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String role;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Boolean isOnline;
}
