package top.aiolife.record.pojo.vo;

import lombok.Data;

/**
 * GitHub 最近提交
 *
 * @author Lys
 * @date 2026/06/13
 */
@Data
public class GithubCommitVO {

    /**
     * 提交 SHA
     */
    private String id;

    /**
     * 仓库名称
     */
    private String repo;

    /**
     * 仓库 URL
     */
    private String repoUrl;

    /**
     * 提交 URL
     */
    private String commitUrl;

    /**
     * 提交信息
     */
    private String message;

    /**
     * 提交时间
     */
    private String date;

    /**
     * 提交者头像
     */
    private String avatar;

    /**
     * 提交者登录名
     */
    private String actor;
}
