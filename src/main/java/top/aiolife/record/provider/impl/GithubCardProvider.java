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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

                // Calculate streak
                int streak = calculateCurrentStreak(contributionCalendar);
                card.setTotalValue(streak + " 天");

                // Calculate today's contributions
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

    private int calculateCurrentStreak(JSONObject calendar) {
        JSONArray weeks = calendar.getJSONArray("weeks");
        List<JSONObject> allDays = new ArrayList<>();
        
        for (int i = 0; i < weeks.size(); i++) {
            JSONArray days = weeks.getJSONObject(i).getJSONArray("contributionDays");
            for (int j = 0; j < days.size(); j++) {
                allDays.add(days.getJSONObject(j));
            }
        }
        
        // Sort by date descending (although it should be sorted already, just to be safe)
        // But the structure is weeks -> days, so it is naturally ascending. 
        // We will traverse backwards.
        
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        int streak = 0;

        // Filter days up to today
        List<JSONObject> validDays = new ArrayList<>();
        for (JSONObject day : allDays) {
            if (day.getString("date").compareTo(today) <= 0) {
                validDays.add(day);
            }
        }
        
        if (validDays.isEmpty()) {
            return 0;
        }

        // Check from the last day (today) backwards
        for (int i = validDays.size() - 1; i >= 0; i--) {
            JSONObject day = validDays.get(i);
            int count = day.getIntValue("contributionCount");
            String date = day.getString("date");

            if (count > 0) {
                streak++;
            } else {
                // If today has 0 contributions, streak is still 0 unless we want to be lenient and check yesterday
                // But usually "current streak" implies active streak. 
                // However, standard logic: if today is 0, we check if yesterday had contributions.
                // If yesterday had contributions, the streak is kept but doesn't include today yet (or maybe it counts up to yesterday).
                // Let's follow common logic: if today is 0, check yesterday. If yesterday > 0, streak starts from yesterday.
                // If today > 0, streak starts from today.
                
                if (date.equals(today)) {
                    continue; // Skip today if 0, check yesterday
                } else {
                    break; // Break on first 0 (excluding today)
                }
            }
        }
        
        return streak;
    }

    private int getTodayContributions(JSONObject calendar) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        JSONArray weeks = calendar.getJSONArray("weeks");
        for (int i = 0; i < weeks.size(); i++) {
            JSONArray days = weeks.getJSONObject(i).getJSONArray("contributionDays");
            for (int j = 0; j < days.size(); j++) {
                JSONObject day = days.getJSONObject(j);
                if (today.equals(day.getString("date"))) {
                    return day.getIntValue("contributionCount");
                }
            }
        }
        return 0;
    }
}
