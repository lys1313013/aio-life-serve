package top.aiolife.record.provider.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.aiolife.record.pojo.vo.DashboardCardVO;
import top.aiolife.record.provider.DashboardCardProvider;
import top.aiolife.sso.mapper.UserMapper;
import top.aiolife.sso.pojo.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * GitHub 卡片提供者
 *
 * @author Lys
 * @date 2026/02/17
 */
@Slf4j
@Component
@AllArgsConstructor
public class GithubCardProvider implements DashboardCardProvider {

    private final UserMapper userMapper;

    @Override
    public String getType() {
        return "GITHUB";
    }

    @Override
    public String getTitle() {
        return "GitHub";
    }

    @Override
    public String getTotalTitle() {
        return "连续提交";
    }

    @Override
    public String getIcon() {
        return "mdi:github";
    }

    @Override
    public int getOrder() {
        return 25;
    }

    @Override
    public DashboardCardVO getCard(int userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null || user.getGithubUsername() == null || user.getGithubToken() == null) {
            return null;
        }

        DashboardCardVO card = new DashboardCardVO();
        card.setType(getType());
        card.setIcon(getIcon());
        card.setIconClickUrl("https://github.com/" + user.getGithubUsername());
        card.setTitle(getTitle());
        card.setTotalTitle(getTotalTitle());

        try {
            JSONObject data = fetchGithubData(user.getGithubUsername(), user.getGithubToken());
            if (data != null && data.containsKey("data")) {
                JSONObject contributionCalendar = data.getJSONObject("data")
                        .getJSONObject("user")
                        .getJSONObject("contributionsCollection")
                        .getJSONObject("contributionCalendar");

                // 计算连续提交天数
                int streak = calculateCurrentStreak(contributionCalendar);
                card.setTotalValue(streak + " 天");

                // 计算今日提交数
                int todayContributions = getTodayContributions(contributionCalendar);
                card.setValue(todayContributions + "");
                card.setValueColor(todayContributions > 0 ? "#3FB27F" : "red");
                card.setTitleClickUrl("https://github.com/" + user.getGithubUsername());
            } else {
                card.setValue("获取失败");
                log.error("GitHub API response invalid: {}", data);
            }
        } catch (Exception e) {
            log.error("获取 GitHub 数据失败", e);
            card.setValue("获取失败");
        }
        
        return card;
    }

    /**
     * 获取 GitHub 数据
     *
     * @param username GitHub 用户名
     * @param token    GitHub Token
     * @return GitHub 数据
     */
    private JSONObject fetchGithubData(String username, String token) {
        String query = "{ \"query\": \"query { user(login: \\\"" + username + "\\\") { contributionsCollection { contributionCalendar { totalContributions weeks { contributionDays { contributionCount date } } } } } }\" }";
        
        try (HttpResponse response = HttpRequest.post("https://api.github.com/graphql")
                .header("Authorization", "Bearer " + token)
                .body(query)
                .execute()) {
            if (response.isOk()) {
                return JSON.parseObject(response.body());
            } else {
                log.error("GitHub API error: status={}, body={}", response.getStatus(), response.body());
            }
        }
        return null;
    }

    /**
     * 计算当前连续提交天数
     *
     * @param calendar 贡献日历
     * @return 连续提交天数
     */
    private int calculateCurrentStreak(JSONObject calendar) {
        JSONArray weeks = calendar.getJSONArray("weeks");
        List<JSONObject> allDays = new ArrayList<>();
        
        for (int i = 0; i < weeks.size(); i++) {
            JSONArray days = weeks.getJSONObject(i).getJSONArray("contributionDays");
            for (int j = 0; j < days.size(); j++) {
                allDays.add(days.getJSONObject(j));
            }
        }
        
        // 此时数据按日期升序排列（weeks -> days），我们将倒序遍历
        
        if (allDays.isEmpty()) {
            return 0;
        }

        int streak = 0;

        // 从最后一天（今天）倒序检查
        for (int i = allDays.size() - 1; i >= 0; i--) {
            JSONObject day = allDays.get(i);
            int count = day.getIntValue("contributionCount");
            
            if (count > 0) {
                streak++;
            } else {
                // 如果是最后记录的一天（根据时区可能被视为“今天”或“昨天”）且提交数为 0，
                // 我们允许跳过它（不中断连续记录，也不增加计数）。
                // 通常逻辑：
                // - 如果今天有提交：连续天数包含今天。
                // - 如果今天无提交：连续天数维持昨天的状态。
                // - 如果昨天无提交：连续天数归零。
                
                // 仅处理最后一天为 0 的情况
                if (i == allDays.size() - 1) {
                    continue; 
                } else {
                    break; 
                }
            }
        }
        
        return streak;
    }

    /**
     * 获取今日提交数
     *
     * @param calendar 贡献日历
     * @return 今日提交数
     */
    private int getTodayContributions(JSONObject calendar) {
        JSONArray weeks = calendar.getJSONArray("weeks");
        JSONObject lastDay = null;

        for (int i = 0; i < weeks.size(); i++) {
            JSONArray days = weeks.getJSONObject(i).getJSONArray("contributionDays");
            if (!days.isEmpty()) {
                lastDay = days.getJSONObject(days.size() - 1);
            }
        }
        
        // 始终使用 GitHub 返回的最后一天作为“今天”
        // 处理时区差异和系统时钟不匹配的问题（例如系统是 2026，GitHub 是 2025）
        if (lastDay != null) {
            return lastDay.getIntValue("contributionCount");
        }
        
        return 0;
    }
}
