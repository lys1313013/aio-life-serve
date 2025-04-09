package com.lys.record.api;

import cn.hutool.json.JSONUtil;
import com.lys.record.pojo.entity.LeetcodeCalendarEntity;
import com.lys.record.pojo.leetcode.UserCalendarResponse;
import com.lys.record.service.ILeetcodeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/leetcode")
public class LeetcodeController {

    private RestTemplate restTemplate;

    private ILeetcodeService leetcodeService;

    @GetMapping("/heatmap")
    public Object fetchAndSaveUserCalendar(String userSlug) {
        String url = "https://leetcode.cn/graphql/noj-go/";

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("operationName", "userProfileCalendar");

        Map<String, Object> variables = new HashMap<>();
        variables.put("userSlug", userSlug);
        requestBody.put("variables", variables);

        requestBody.put("query", """
            query userProfileCalendar($userSlug: String!, $year: Int) {
              userCalendar(userSlug: $userSlug, year: $year) {
                streak
                totalActiveDays
                submissionCalendar
                activeYears
                monthlyMedals {
                  name
                  obtainDate
                  category
                  config {
                    icon
                    iconGif
                    iconGifBackground
                  }
                  progress
                  id
                  year
                  month
                }
                recentStreak
              }
            }
            """);

        // todo 待替换
        Integer userId = 13;

        // 发送请求并获取响应
        String response = restTemplate.postForObject(url, requestBody, String.class);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, null);

        ResponseEntity<UserCalendarResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<UserCalendarResponse>() {
                });
        UserCalendarResponse body = responseEntity.getBody();
        String submissionCalendar = body.getData().getUserCalendar().getSubmissionCalendar();
        Map<String, Object> sumbissonMap = JSONUtil.parseObj(submissionCalendar).toBean(Map.class);
        if (sumbissonMap == null) {
            log.error("用户{}的leetcode热力图为空", userSlug);
            return null;
        }

        LeetcodeCalendarEntity leetcodeCalendarEntity;

        // 最小的日期
        LocalDate startDate = null;
        // 最大的日期
        LocalDate endDate = null;

        List<LeetcodeCalendarEntity> leetcodeCalendarEntities = new ArrayList<>();
        for (Map.Entry<String, Object> entry : sumbissonMap.entrySet()) {
            leetcodeCalendarEntity = new LeetcodeCalendarEntity();
            // 将时间戳字符串转换为 long
            long timestamp = Long.parseLong(entry.getKey());
            // 将时间戳转换为 Instant
            Instant instant = Instant.ofEpochSecond(timestamp);
            // 将 Instant 转换为 LocalDate
            LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

            if (endDate == null) {
                endDate = localDate;
                startDate = localDate;
            }
            // endDate 赋值最大
            if (localDate.isAfter(endDate)) {
                endDate = localDate;
            }
            if (localDate.isBefore(startDate)) {
                startDate = localDate;
            }

            leetcodeCalendarEntity.setUserId(userId);
            leetcodeCalendarEntity.setSubmitDate(localDate);
            leetcodeCalendarEntity.setSubmitCount((Integer) entry.getValue());
            leetcodeCalendarEntities.add(leetcodeCalendarEntity);
        }

        leetcodeService.delete(userId, startDate, endDate);
        leetcodeService.batchInsert(leetcodeCalendarEntities);
        return leetcodeCalendarEntities;
    }
}
