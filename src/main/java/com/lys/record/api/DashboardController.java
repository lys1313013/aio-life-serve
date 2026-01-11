package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.lys.core.resq.ApiResponse;
import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.record.service.IExerciseRecordService;
import com.lys.record.service.ILeetcodeService;
import com.lys.record.service.ITimeRecordService;
import com.lys.sso.mapper.UserMapper;
import com.lys.sso.pojo.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 看板
 *
 * @author Lys
 * @date 2025/04/13 13:56
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
    private final ILeetcodeService leetcodeService;

    private final IExerciseRecordService exerciseRecordService;

    private final ITimeRecordService timeRecordService;

    private final UserMapper userMapper;

    /**
     * 看板卡片
     */
    @PostMapping("/card")
    public ApiResponse<List<DashboardCardVO>> card() {
        long totalStart = System.currentTimeMillis();
        int userId = StpUtil.getLoginIdAsInt();

        List<DashboardCardVO> dashboardCardList = new ArrayList<>();
        // 获取今年已过
        long yearStart = System.currentTimeMillis();
        dashboardCardList.add(getYearPassedCard((long) userId));
        log.info("获取今年已过卡片耗时: {}ms", System.currentTimeMillis() - yearStart);

        // 异步获取运动卡片
        CompletableFuture<DashboardCardVO> exerciseCardFuture = CompletableFuture.supplyAsync(() -> {
            long exerciseStart = System.currentTimeMillis();
            DashboardCardVO card = getExerciseCard(userId);
            log.info("异步获取运动卡片任务耗时: {}ms", System.currentTimeMillis() - exerciseStart);
            return card;
        });

        try {
            // 同步leetcode信息
            UserEntity userEntity = userMapper.selectById(userId);

            // 异步获取今日提交次数
            CompletableFuture<Integer> submissionCountFuture = CompletableFuture.supplyAsync(() -> {
                long submissionStart = System.currentTimeMillis();
                Integer count = leetcodeService.getTodaySubmissionCount(userEntity.getLeetcodeAcct());
                log.info("异步获取 LeetCode 今日提交次数任务耗时: {}ms", System.currentTimeMillis() - submissionStart);
                return count;
            });

            long leetcodeCheckStart = System.currentTimeMillis();
            Pair<Boolean, String> leetcodeResult = leetcodeService.checkToday(userEntity, false);
            log.info("同步获取 LeetCode 今日状态耗时: {}ms", System.currentTimeMillis() - leetcodeCheckStart);

            DashboardCardVO dashboardCardVO = new DashboardCardVO();
            dashboardCardVO.setIcon("devicon:leetcode");
            dashboardCardVO.setIconClickUrl("https://leetcode.cn/u/" + userEntity.getLeetcodeAcct());
            dashboardCardVO.setTitle("每日一题");
            dashboardCardVO.setTitleClickUrl(leetcodeResult.getSecond());
            if (leetcodeResult.getFirst()) {
                dashboardCardVO.setValue("已完成");
            } else {
                dashboardCardVO.setValue("未完成");
                dashboardCardVO.setValueColor("red");
            }

            dashboardCardVO.setTotalTitle("今日提交");

            // 获取异步结果，超时时间设置为3秒
            Integer submissionCount = 0;
            try {
                long waitSubmissionStart = System.currentTimeMillis();
                submissionCount = submissionCountFuture.get(3, TimeUnit.SECONDS);
                log.info("等待 LeetCode 提交次数异步结果耗时: {}ms", System.currentTimeMillis() - waitSubmissionStart);
                dashboardCardVO.setTotalValue(String.valueOf(submissionCount));
            } catch (Exception e) {
                dashboardCardVO.setTotalValue("获取失败");
                log.error("异步获取 LeetCode 今日提交次数失败", e);
            }

            // 获取运动卡片结果
            try {
                long waitExerciseStart = System.currentTimeMillis();
                DashboardCardVO exerciseCard = exerciseCardFuture.get(3, TimeUnit.SECONDS);
                log.info("等待运动卡片异步结果耗时: {}ms", System.currentTimeMillis() - waitExerciseStart);
                dashboardCardList.add(exerciseCard);
            } catch (Exception e) {
                log.error("异步获取运动卡片失败", e);
                // 如果异步获取失败，可以添加一个默认的错误卡片 or 直接忽略
            }

            dashboardCardList.add(dashboardCardVO);
        } catch (Exception e) {
            log.error("获取看板卡片失败", e);
        }

        log.info("看板卡片接口总耗时: {}ms", System.currentTimeMillis() - totalStart);
        return ApiResponse.success(dashboardCardList);
    }

    /**
     * 获取运动卡片
     */
    private DashboardCardVO getExerciseCard(int userId) {
        DashboardCardVO exerciseCard = new DashboardCardVO();
        exerciseCard.setIcon("mdi:run");
        exerciseCard.setTitle("今日运动");
        try {
            long countStart = System.currentTimeMillis();
            int todayExerciseTypes = exerciseRecordService.countTodayExerciseTypes((long) userId);
            log.info("获取今日运动类型计数耗时: {}ms", System.currentTimeMillis() - countStart);

            exerciseCard.setValue(String.valueOf(todayExerciseTypes));
            if (todayExerciseTypes == 0) {
                exerciseCard.setValueColor("red");
            }
            exerciseCard.setTotalTitle("连续运动");

            long consecutiveStart = System.currentTimeMillis();
            exerciseCard.setTotalValue(exerciseRecordService.getConsecutiveExerciseDays((long) userId) + " 天");
            log.info("获取连续运动天数耗时: {}ms", System.currentTimeMillis() - consecutiveStart);
        } catch (Exception e) {
            log.error("获取运动数据失败", e);
            exerciseCard.setValue("获取失败");
            exerciseCard.setTotalValue("获取失败");
        }
        return exerciseCard;
    }

    /**
     * 获取今年已过的天数
     */
    private int getDaysPassed() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfYear = LocalDate.of(today.getYear(), 1, 1);
        return (int) ChronoUnit.DAYS.between(firstDayOfYear, today) + 1;
    }

    /**
     * 获取今年已过卡片
     */
    private DashboardCardVO getYearPassedCard(Long userId) {
        int daysPassed = getDaysPassed();
        DashboardCardVO dashboardCardVO = new DashboardCardVO();
        dashboardCardVO.setTitle("当前状态");
        dashboardCardVO.setIcon("tdesign:time");
        long lastRecordStart = System.currentTimeMillis();
        dashboardCardVO.setValue(timeRecordService.getLastRecordTimeDiff(userId));
        log.info("获取最后一条记录时间差耗时: {}ms", System.currentTimeMillis() - lastRecordStart);

        dashboardCardVO.setTotalTitle("当年已过");
        dashboardCardVO.setTotalValue(daysPassed + " / " + (LocalDate.now().lengthOfYear()) +" 天");
        return dashboardCardVO;
    }
}
