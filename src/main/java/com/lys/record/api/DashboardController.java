package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.lys.core.resq.ApiResponse;
import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.record.service.IExerciseRecordService;
import com.lys.record.service.ILeetcodeService;
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

    private final UserMapper userMapper;

    /**
     * 看板卡片
     */
    @PostMapping("/card")
    public ApiResponse<List<DashboardCardVO>> card() {
        int userId = StpUtil.getLoginIdAsInt();

        List<DashboardCardVO> dashboardCardList = new ArrayList<>();
        // 获取今年已过
        dashboardCardList.add(getYearPassedCard());

        // 异步获取运动卡片
        CompletableFuture<DashboardCardVO> exerciseCardFuture = CompletableFuture.supplyAsync(() ->
                getExerciseCard(userId)
        );

        try {
            // 同步leetcode信息
            UserEntity userEntity = userMapper.selectById(userId);

            // 异步获取今日提交次数
            CompletableFuture<Integer> submissionCountFuture = CompletableFuture.supplyAsync(() ->
                    leetcodeService.getTodaySubmissionCount(userEntity.getLeetcodeAcct())
            );

            Pair<Boolean, String> leetcodeResult = leetcodeService.checkToday(userEntity, false);
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
                submissionCount = submissionCountFuture.get(3, TimeUnit.SECONDS);
                dashboardCardVO.setTotalValue(String.valueOf(submissionCount));
            } catch (Exception e) {
                dashboardCardVO.setTotalValue("获取失败");
                log.error("异步获取 LeetCode 今日提交次数失败", e);
            }

            // 获取运动卡片结果
            try {
                DashboardCardVO exerciseCard = exerciseCardFuture.get(3, TimeUnit.SECONDS);
                dashboardCardList.add(exerciseCard);
            } catch (Exception e) {
                log.error("异步获取运动卡片失败", e);
                // 如果异步获取失败，可以添加一个默认的错误卡片或直接忽略
            }

            dashboardCardList.add(dashboardCardVO);
        } catch (Exception e) {
            log.error("获取看板卡片失败", e);
        }

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
            int todayExerciseTypes = exerciseRecordService.countTodayExerciseTypes((long) userId);
            exerciseCard.setValue(String.valueOf(todayExerciseTypes));
            if (todayExerciseTypes == 0) {
                exerciseCard.setValueColor("red");
            }
            exerciseCard.setTotalTitle("连续运动");
            exerciseCard.setTotalValue(String.valueOf(exerciseRecordService.getConsecutiveExerciseDays((long) userId)));
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
    private DashboardCardVO getYearPassedCard() {
        int daysPassed = getDaysPassed();
        DashboardCardVO dashboardCardVO = new DashboardCardVO();
        dashboardCardVO.setTitle("今年已过");
        dashboardCardVO.setIcon("tdesign:time");
        dashboardCardVO.setValue("" + daysPassed);
        dashboardCardVO.setTotalTitle("剩余天数");
        dashboardCardVO.setTotalValue("" + (LocalDate.now().lengthOfYear() - daysPassed));
        return dashboardCardVO;
    }
}
