package com.lys.record.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lys.core.util.SysUtil;
import com.lys.record.mapper.LeetcodeCalendarMapper;
import com.lys.record.pojo.entity.LeetcodeCalendarEntity;
import com.lys.record.pojo.leetcode.UserCalendarResponse;
import com.lys.record.service.ILeetcodeService;
import com.lys.record.service.IMailService;
import com.lys.sso.mapper.UserMapper;
import com.lys.sso.pojo.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/10 00:06
 */
@Slf4j
@Service
@AllArgsConstructor
public class LeetcodeServiceImpl extends ServiceImpl<LeetcodeCalendarMapper, LeetcodeCalendarEntity> implements ILeetcodeService {

    private LeetcodeCalendarMapper leetcodeCalendarMapper;

    private UserMapper userMapper;

    private RestTemplate restTemplate;

    private IMailService mailService;

    @Override
    public int delete(Integer userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<LeetcodeCalendarEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeetcodeCalendarEntity::getUserId, userId)
                .ge(LeetcodeCalendarEntity::getSubmitDate, startDate)
                .le(LeetcodeCalendarEntity::getSubmitDate, endDate);
       return leetcodeCalendarMapper.delete(queryWrapper);
    }


    @Override
    public boolean batchInsert(List<LeetcodeCalendarEntity> leetcodeList) {
        return this.saveOrUpdateBatch(leetcodeList);
    }

    @Override
    public void check() {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNotNull(UserEntity::getLeetcodeAcct);
        List<UserEntity> userEntityList = userMapper.selectList(queryWrapper);

        for (UserEntity userEntity : userEntityList) {
            String leetcodeAcct = userEntity.getLeetcodeAcct();
            LambdaQueryWrapper<LeetcodeCalendarEntity> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(LeetcodeCalendarEntity::getUserId, userEntity.getId());
            List<LeetcodeCalendarEntity> leetcodeCalendarEntityList = leetcodeCalendarMapper.selectList(queryWrapper1);
            if (SysUtil.isNotEmpty(leetcodeCalendarEntityList)) {
                List<LeetcodeCalendarEntity> leetcodeCalendarEntities = fetchAndSaveUserCalendar(leetcodeAcct, userEntity.getId());
                // 如果当天未提交记录，则发邮件
                boolean flag = false;
                for (LeetcodeCalendarEntity leetcodeCalendarEntity : leetcodeCalendarEntities) {
                    LocalDate submitDate = leetcodeCalendarEntity.getSubmitDate();
                    if (submitDate.isEqual(LocalDate.now())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    mailService.sendSimpleEmail(userEntity.getEmail(), "leetcode咋还没刷", "leetcode咋还没刷");
                }
            }

        }
    }

    public List<LeetcodeCalendarEntity> fetchAndSaveUserCalendar(String userSlug, Integer userId) {
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

        // 发送请求并获取响应

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, null);

        ResponseEntity<UserCalendarResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<UserCalendarResponse>() {
                });
        UserCalendarResponse body = responseEntity.getBody();
        String submissionCalendar = body.getData().getUserCalendar().getSubmissionCalendar();
        Map<String, Object> sumbissonMap = JSONUtil.parseObj(submissionCalendar).toBean(Map.class);
        if (sumbissonMap == null) {
            log.info("用户{}的leetcode热力图为空", userSlug);
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

        this.delete(userId, startDate, endDate);
        this.batchInsert(leetcodeCalendarEntities);
        return leetcodeCalendarEntities;
    }
}
