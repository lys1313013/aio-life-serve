package top.aiolife.feedback.pojo.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 管理员用户 VO（轻量级，用于配置下拉选择）
 *
 * @author Ethan
 * @date 2026/07/19
 */
@Getter
@Setter
public class AdminUserVO {

    private String id;

    private String username;

    private String nickname;
}
