package top.aiolife.record.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.aiolife.record.pojo.vo.GithubCommitVO;
import top.aiolife.record.service.IGithubService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2026-01-02 23:24
 */
@Slf4j
@Service
public class GithubServiceImpl implements IGithubService {

    @Override
    public List<GithubCommitVO> searchRecentCommits(String username, String token, int page, int perPage) {
        if (username == null || username.isEmpty()) {
            return Collections.emptyList();
        }
        if (page <= 0) {
            page = 1;
        }
        if (perPage <= 0) {
            perPage = 20;
        }
        if (perPage > 100) {
            perPage = 100;
        }

        String url = "https://api.github.com/search/commits"
                + "?q=author:" + username
                + "&sort=committer-date"
                + "&order=desc"
                + "&page=" + page
                + "&per_page=" + perPage;

        HttpRequest request = HttpRequest.get(url)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .header("User-Agent", "aio-life-server");
        if (token != null && !token.isEmpty()) {
            request.header("Authorization", "Bearer " + token);
        }

        try (HttpResponse response = request.execute()) {
            if (!response.isOk()) {
                log.error("GitHub search commits failed: status={}, body={}",
                        response.getStatus(), response.body());
                return Collections.emptyList();
            }
            JSONObject body = JSON.parseObject(response.body());
            JSONArray items = body.getJSONArray("items");
            if (items == null || items.isEmpty()) {
                return Collections.emptyList();
            }

            List<GithubCommitVO> result = new ArrayList<>(items.size());
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject repo = item.getJSONObject("repository");
                JSONObject commit = item.getJSONObject("commit");
                JSONObject author = item.getJSONObject("author");
                JSONObject commitAuthor = commit == null ? null : commit.getJSONObject("author");

                GithubCommitVO vo = new GithubCommitVO();
                vo.setId(item.getString("sha"));
                vo.setRepo(repo == null ? null : repo.getString("name"));
                vo.setRepoUrl(repo == null ? null : repo.getString("html_url"));
                vo.setCommitUrl(item.getString("html_url"));
                vo.setMessage(commit == null ? null : commit.getString("message"));
                vo.setDate(commitAuthor == null ? null : commitAuthor.getString("date"));
                vo.setAvatar(author == null ? null : author.getString("avatar_url"));
                vo.setActor(author == null ? username : author.getString("login"));
                result.add(vo);
            }
            return result;
        } catch (Exception e) {
            log.error("调用 GitHub 搜索提交接口异常", e);
            return Collections.emptyList();
        }
    }
}
