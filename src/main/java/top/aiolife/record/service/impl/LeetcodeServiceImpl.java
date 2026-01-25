package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.aiolife.core.util.DateUtil;
import top.aiolife.record.client.LeetcodeClient;
import top.aiolife.record.pojo.leetcode.QuestionDataResponse;
import top.aiolife.record.pojo.leetcode.RecentACSubmissionsResponse;
import top.aiolife.record.pojo.leetcode.TodayRecordResponse;
import top.aiolife.record.pojo.leetcode.UserCalendarResponse;
import top.aiolife.record.pojo.vo.DashboardCardVO;
import top.aiolife.record.service.ILeetcodeService;
import top.aiolife.record.service.IMailService;
import top.aiolife.record.util.RedisUtil;
import top.aiolife.sso.mapper.UserMapper;
import top.aiolife.sso.pojo.entity.UserEntity;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/10 00:06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeetcodeServiceImpl implements ILeetcodeService {

    private final UserMapper userMapper;

    private final RestTemplate restTemplate;

    private final IMailService mailService;

    private final RedisUtil redisUtil;

    private final LeetcodeClient leetcodeClient;

    private final CacheManager cacheManager;

    private final ObjectMapper objectMapper;

    private Cache localCache;

    @PostConstruct
    public void init() {
        this.localCache = cacheManager.getCache("leetcode");
    }

    /**
     * 多级缓存通用获取方法
     *
     * @param cacheKey 缓存键
     * @param clazz    返回类型
     * @param supplier 真正获取数据的方法
     * @param <T>      泛型
     * @return 数据
     */
    private <T> T getWithCache(String cacheKey, Class<T> clazz, Supplier<T> supplier) {
        // 1. 查询本地缓存
        T cached = localCache.get(cacheKey, clazz);
        if (cached != null) {
            log.info("多级缓存：命中本地缓存 key={}", cacheKey);
            return cached;
        }

        // 2. 查询 Redis 缓存
        String redisKey = "leetcode:cache:" + cacheKey;
        cached = redisUtil.getObject(redisKey, clazz);
        if (cached != null) {
            log.info("多级缓存：命中 Redis 缓存 key={}", redisKey);
            // 回写本地缓存
            localCache.put(cacheKey, cached);
            return cached;
        }

        // 3. 查询接口/数据库
        T result = supplier.get();
        if (result != null) {
            log.info("多级缓存：查询原始数据并回写缓存 key={}", cacheKey);
            // 回写 Redis 缓存（24小时过期）
            redisUtil.setObject(redisKey, result, 24, TimeUnit.HOURS);
            // 回写本地缓存
            localCache.put(cacheKey, result);
        }
        return result;
    }

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
    public Pair<Boolean, String> checkToday(UserEntity userEntity, boolean sendEmail) {
        // 查询每日一题
        long start = System.currentTimeMillis();
        String todayTitleSlug = getTodayQuestionTitleSlug();
        long end = System.currentTimeMillis();
        log.info("查询每日一题耗时: {} ms", end - start);
        String todayUrl = "https://leetcode.cn/problems/" + todayTitleSlug + "/";

        // Redis 缓存逻辑
        String redisKey = "leetcode:checkin:" + DateUtil.getNowFormatDate() + ":" + userEntity.getId();
        if (redisUtil.hasKey(redisKey)) {
            return Pair.of(true, todayUrl);
        }

        String leetcodeAcct = userEntity.getLeetcodeAcct();

        // 查询最近提交
        RecentACSubmissionsResponse recentACSubmissions = this.fetchRecentAcSubmissions(leetcodeAcct);
        List<RecentACSubmissionsResponse.RecentACSubmission> submissions = recentACSubmissions.getData().getRecentACSubmissions();

        // 比对是否完成每日一题
        boolean finishedToday = false;
        if (submissions != null) {
            for (RecentACSubmissionsResponse.RecentACSubmission submission : submissions) {
                if (todayTitleSlug.equals(submission.getQuestion().getTitleSlug())) {
                    finishedToday = true;
                    // 如果今天已经打卡过了，则增加一个由当天时间 + 用户 ID 的锁，过期时间为24小时
                    redisUtil.set(redisKey, "1", 24, TimeUnit.HOURS);
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
                    log.info("给 userId: {} 发送邮件提醒", userEntity.getId());
                    mailService.sendSimpleEmail(userEntity.getEmail(), "leetcode咋还没刷", "leetcode咋还没刷");
                } catch (Exception e) {
                    log.error("发送邮件失败", e);
                }
            } else {
                log.info("周一到周五19点前不发送邮件提醒，当前时间：{} {}", dayOfWeek, currentTime);
            }
        }
        return Pair.of(finishedToday, todayUrl);
    }

    @Override
    public Integer getTodaySubmissionCount(String leetcodeAcct) {
        if (leetcodeAcct == null || leetcodeAcct.isEmpty()) {
            return 0;
        }
        UserCalendarResponse userCalendar = leetcodeClient.getUserCalendar(leetcodeAcct);
        if (userCalendar == null || userCalendar.getData() == null || userCalendar.getData().getUserCalendar() == null) {
            return 0;
        }
        String submissionCalendar = userCalendar.getData().getUserCalendar().getSubmissionCalendar();
        if (submissionCalendar == null || submissionCalendar.isEmpty()) {
            return 0;
        }

        try {
            Map<String, Integer> calendarMap = objectMapper.readValue(submissionCalendar, new TypeReference<Map<String, Integer>>() {
            });

            // 获取今天的 0 点时间戳（秒），LeetCode 使用的是 UTC 时间戳
            long todayStartTimestamp = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

            return calendarMap.getOrDefault(String.valueOf(todayStartTimestamp), 0);
        } catch (Exception e) {
            log.error("解析 LeetCode 提交日历失败", e);
            return 0;
        }
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
        String todayTitleSlug = getTodayQuestionTitleSlug();
        String cacheKey = "leetcode:question:" + todayTitleSlug;
        return getWithCache(cacheKey, QuestionDataResponse.class, () -> leetcodeClient.getQuestionData(todayTitleSlug));
    }

    /**
     * 获取每日一题的题目 slug
     */
    public String getTodayQuestionTitleSlug() {
        String cacheKey = "leetcode:today_title_slug:" + DateUtil.getNowFormatDate();
        return getWithCache(cacheKey, String.class, () -> {
            TodayRecordResponse todayRecord = leetcodeClient.getTodayRecord();
            return todayRecord.getData().getTodayRecord().get(0).getQuestion().getTitleSlug();
        });
    }
}
