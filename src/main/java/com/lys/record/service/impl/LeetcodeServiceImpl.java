package com.lys.record.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lys.core.util.DateUtil;
import com.lys.core.util.SysUtil;
import com.lys.record.client.LeetcodeClient;
import com.lys.record.mapper.ILeetcodeUserInfoMapper;
import com.lys.record.mapper.LeetcodeCalendarMapper;
import com.lys.record.pojo.entity.LeetcodeCalendarEntity;
import com.lys.record.pojo.entity.LeetcodeUserInfoEntity;
import com.lys.record.pojo.leetcode.QuestionDataResponse;
import com.lys.record.pojo.leetcode.TodayRecordResponse;
import com.lys.record.pojo.leetcode.UserCalendarResponse;
import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.record.service.ILeetcodeService;
import com.lys.record.service.IMailService;
import com.lys.record.util.RedisUtil;
import com.lys.sso.mapper.UserMapper;
import com.lys.sso.pojo.entity.UserEntity;
import jakarta.mail.MessagingException;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    private ILeetcodeUserInfoMapper leetcodeUserInfoMapper;

    private UserMapper userMapper;

    private RestTemplate restTemplate;

    private IMailService mailService;

    private RedisUtil redisUtil;

    private LeetcodeClient leetcodeClient;

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
        return super.saveOrUpdateBatch(leetcodeList);
    }

    @Override
    public void check() {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNotNull(UserEntity::getLeetcodeAcct);
        List<UserEntity> userEntityList = userMapper.selectList(queryWrapper);

        for (UserEntity userEntity : userEntityList) {
            syncLeetcodeInfo(userEntity);
        }
    }

    public void syncLeetcodeInfo(UserEntity userEntity) {
        // 控制查询leetcode频率
        String redisKey = "syncLeetcodeInfo:" + userEntity.getId();
        if (redisUtil.hasKey(redisKey)) {
            log.info("用户{}的leetcode信息已经同步过了", userEntity.getLeetcodeAcct());
            return;
        }
        String leetcodeAcct = userEntity.getLeetcodeAcct();
        LambdaQueryWrapper<LeetcodeCalendarEntity> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(LeetcodeCalendarEntity::getUserId, userEntity.getId());
        List<LeetcodeCalendarEntity> leetcodeCalendarEntityList = leetcodeCalendarMapper.selectList(queryWrapper1);
        if (SysUtil.isNotEmpty(leetcodeCalendarEntityList)) {
            Integer userId = userEntity.getId();
            // 查询leetcode信息
            UserCalendarResponse body = fetchAndSaveUserCalendar(leetcodeAcct);

            String submissionCalendar = body.getData().getUserCalendar().getSubmissionCalendar();
            Map<String, Object> sumbissonMap = JSONUtil.parseObj(submissionCalendar).toBean(Map.class);
            if (sumbissonMap == null) {
                log.info("用户{}的leetcode热力图为空", leetcodeAcct);
                redisUtil.set(redisKey, redisKey, 5, TimeUnit.MINUTES);
                return;
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

            // 保存统计信息
            saveUserInfo(leetcodeCalendarEntities, body, userId);

            // 如果当天未提交记录，则发邮件
            boolean flag = false;
            for (LeetcodeCalendarEntity item : leetcodeCalendarEntities) {
                LocalDate submitDate = item.getSubmitDate();
                if (submitDate.isEqual(LocalDate.now())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                mailService.sendSimpleEmail(userEntity.getEmail(), "leetcode咋还没刷", "leetcode咋还没刷");
            }
        }
        redisUtil.set(redisKey, redisKey, 5, TimeUnit.MINUTES);
    }

    private void saveUserInfo(List<LeetcodeCalendarEntity> leetcodeCalendarEntityList, UserCalendarResponse body, Integer userId) {
        // 统计总提交数
        int totalSubmissionCount = leetcodeCalendarEntityList.stream().mapToInt(LeetcodeCalendarEntity::getSubmitCount).sum();

        UserCalendarResponse.UserCalendar userCalendar = body.getData().getUserCalendar();
        LeetcodeUserInfoEntity leetcodeUserInfoEntity = new LeetcodeUserInfoEntity();
        leetcodeUserInfoEntity.setUserId(userId);
        leetcodeUserInfoEntity.setStreak(userCalendar.getStreak());
        leetcodeUserInfoEntity.setTotalActiveDays(userCalendar.getTotalActiveDays());
        leetcodeUserInfoEntity.setRecentStreak(userCalendar.getRecentStreak());
        leetcodeUserInfoEntity.setTotalSubmissionCount(totalSubmissionCount);
        leetcodeUserInfoMapper.insert(leetcodeUserInfoEntity);
    }

    /**
     * 调用leetcode接口获取数据
     *
     * @param userSlug leetcode 用户id
     * @return leetcode返回结果
     */
    public UserCalendarResponse fetchAndSaveUserCalendar(String userSlug) {
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
        return responseEntity.getBody();
    }


    @Override
    public List<DashboardCardVO> getDashboardCard(int userId) {
        LambdaQueryWrapper<LeetcodeUserInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeetcodeUserInfoEntity::getUserId, userId);
        queryWrapper.orderByDesc(LeetcodeUserInfoEntity::getCreateTime);
        queryWrapper.last("limit 1");
        LeetcodeUserInfoEntity leetcodeUserInfoEntity = leetcodeUserInfoMapper.selectOne(queryWrapper);
        if (leetcodeUserInfoEntity == null) {
            return new ArrayList<>();
        }

        // 打卡信息
        DashboardCardVO dashboardCardVO1 = new DashboardCardVO();
        dashboardCardVO1.setValue(leetcodeUserInfoEntity.getRecentStreak());
        dashboardCardVO1.setTitle("连续打卡天数");

        dashboardCardVO1.setTotalValue(leetcodeUserInfoEntity.getTotalActiveDays());
        dashboardCardVO1.setTotalTitle("最近一年累计打卡天数");

        // 提交量信息
        DashboardCardVO dashboardCardVO2 = new DashboardCardVO();
        LambdaQueryWrapper<LeetcodeCalendarEntity> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(LeetcodeCalendarEntity::getUserId, userId);
        // 查找今天的提交信息
        queryWrapper1.eq(LeetcodeCalendarEntity::getSubmitDate, LocalDate.now());
        LeetcodeCalendarEntity leetcodeCalendarEntity = leetcodeCalendarMapper.selectOne(queryWrapper1);
        if (leetcodeCalendarEntity == null) {
            dashboardCardVO2.setValue(0);
        } else {
            dashboardCardVO2.setValue(leetcodeCalendarEntity.getSubmitCount());
        }
        dashboardCardVO2.setTitle("今天提交量");

        // 待统计
        dashboardCardVO2.setTotalValue(leetcodeUserInfoEntity.getTotalSubmissionCount());
        dashboardCardVO2.setTotalTitle("最近一年累计提交量");

        return Arrays.asList(dashboardCardVO1, dashboardCardVO2);
    }

    @Override
    public void notifyTodayQuestion() throws MessagingException {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNotNull(UserEntity::getEmail);
        queryWrapper.isNotNull(UserEntity::getLeetcodeAcct);
        List<UserEntity> userEntityList = userMapper.selectList(queryWrapper);

        QuestionDataResponse questionData = this.getTodayQuestion();

        String title = "力扣每日一题" + DateUtil.getNowFormatDate() + "：" + questionData.getData().getQuestion().getTranslatedTitle();

        String question = questionData.getData().getQuestion().getTranslatedContent();

        // 邮件内容
        String htmlContent = String.format("""
                %s
                <a href="https://leetcode.cn/problems/%s/"  target="_blank" >%s</a>
                """, question, questionData.getData().getQuestion().getTitleSlug(), questionData.getData().getQuestion().getTranslatedTitle()
        );

        for (UserEntity userEntity : userEntityList) {
            mailService.sendHtmlEmail(userEntity.getEmail(), title, htmlContent);
        }
    }

    public QuestionDataResponse getTodayQuestion() {
        TodayRecordResponse todayRecord = leetcodeClient.getTodayRecord();
        return leetcodeClient.getQuestionData(todayRecord.getData().getTodayRecord().get(0).getQuestion().getTitleSlug());
    }
}
