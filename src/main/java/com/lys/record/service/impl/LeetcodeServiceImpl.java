package com.lys.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lys.core.util.DateUtil;
import com.lys.record.client.LeetcodeClient;
import com.lys.record.pojo.leetcode.QuestionDataResponse;
import com.lys.record.pojo.leetcode.RecentACSubmissionsResponse;
import com.lys.record.pojo.leetcode.TodayRecordResponse;
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

import java.time.LocalDate;
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
public class LeetcodeServiceImpl implements ILeetcodeService {

    private UserMapper userMapper;

    private RestTemplate restTemplate;

    private IMailService mailService;

    private RedisUtil redisUtil;

    private LeetcodeClient leetcodeClient;

    @Override
    public void check() {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNotNull(UserEntity::getLeetcodeAcct);
        List<UserEntity> userEntityList = userMapper.selectList(queryWrapper);

        for (UserEntity userEntity : userEntityList) {
            checkToday(userEntity, true);
        }
    }

    @Override
    public boolean checkToday(UserEntity userEntity, boolean sendEmail) {
        String leetcodeAcct = userEntity.getLeetcodeAcct();

        // 1. 查询每日一题
        QuestionDataResponse todayQuestion = this.getTodayQuestion();
        String todayTitleSlug = todayQuestion.getData().getQuestion().getTitleSlug();

        // 2. 查询最近提交
        RecentACSubmissionsResponse recentACSubmissions = this.fetchRecentAcSubmissions(leetcodeAcct);
        List<RecentACSubmissionsResponse.RecentACSubmission> submissions = recentACSubmissions.getData().getRecentACSubmissions();

        // 3. 比对是否完成每日一题
        boolean finishedToday = false;
        if (submissions != null) {
            for (RecentACSubmissionsResponse.RecentACSubmission submission : submissions) {
                if (todayTitleSlug.equals(submission.getQuestion().getTitleSlug())) {
                    finishedToday = true;
                    break;
                }
            }
        }

        if (sendEmail && !finishedToday) {
            // 获取当前时间和星期几
            LocalDate now = LocalDate.now();
            java.time.DayOfWeek dayOfWeek = now.getDayOfWeek();
            java.time.LocalTime currentTime = java.time.LocalTime.now();

            // 判断是否为周一到周五且时间在19点前
            boolean isWeekday = dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5;
            boolean isBefore7pm = currentTime.isBefore(java.time.LocalTime.of(19, 0));

            // 只有在非工作日或者19点后才发送邮件
            if (!isWeekday || !isBefore7pm) {
                try {
                    mailService.sendSimpleEmail(userEntity.getEmail(), "leetcode咋还没刷", "leetcode咋还没刷");
                } catch (Exception e) {
                    log.error("发送邮件失败", e);
                }
            } else {
                log.info("周一到周五19点前不发送邮件提醒，当前时间：{} {}", dayOfWeek, currentTime);
            }
        }
        return finishedToday;
    }

    public RecentACSubmissionsResponse fetchRecentAcSubmissions(String userSlug) {
        String url = "https://leetcode.cn/graphql/noj-go/";

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> variables = new HashMap<>();
        variables.put("userSlug", userSlug);
        requestBody.put("variables", variables);

        requestBody.put("query", """
                query recentAcSubmissions($userSlug: String!) {
                  recentACSubmissions(userSlug: $userSlug) {
                    submissionId
                    submitTime
                    question {
                      title
                      translatedTitle
                      titleSlug
                      questionFrontendId
                    }
                  }
                }
                """);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, null);

        ResponseEntity<RecentACSubmissionsResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<RecentACSubmissionsResponse>() {
                });
        return responseEntity.getBody();
    }

    @Override
    public List<DashboardCardVO> getDashboardCard(int userId) {
        return new ArrayList<>();
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
