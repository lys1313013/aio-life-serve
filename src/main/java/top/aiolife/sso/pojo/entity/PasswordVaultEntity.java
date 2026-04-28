package top.aiolife.sso.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import top.aiolife.record.pojo.entity.BaseEntity;

/**
 * 密码库表
 *
 * @author Lys
 * @date 2026-04-28
 */
@Getter
@Setter
public class PasswordVaultEntity extends BaseEntity {

    private Long userId;

    /**
     * 标题，如 GitHub
     */
    private String title;

    /**
     * 网站/应用名
     */
    private String website;

    /**
     * 分类：工作/生活/金融/社交/其他
     */
    private String category;

    /**
     * 账号（SM4加密存储）
     */
    private String username;

    /**
     * 密码（SM4加密存储）
     */
    private String password;

    /**
     * PBKDF2盐值，每条记录唯一
     */
    private String salt;

    /**
     * 备注（SM4加密存储）
     */
    private String remark;

    /**
     * 是否收藏
     */
    private Boolean favorite;
}