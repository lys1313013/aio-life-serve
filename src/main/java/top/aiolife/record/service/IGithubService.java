package top.aiolife.record.service;

import top.aiolife.record.pojo.vo.GithubCommitVO;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2026-01-02 23:23
 */
public interface IGithubService {

    /**
     * 获取指定用户最近的提交列表（服务端代理，规避浏览器 CORS）
     *
     * @param username GitHub 用户名
     * @param token    GitHub Access Token（可空）
     * @param perPage  每页数量
     * @return 提交列表
     */
    List<GithubCommitVO> searchRecentCommits(String username, String token, int perPage);
}
