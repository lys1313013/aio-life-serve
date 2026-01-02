package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.lys.core.resq.ApiResponse;
import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.record.service.ILeetcodeService;
import com.lys.sso.mapper.UserMapper;
import com.lys.sso.pojo.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private ILeetcodeService leetcodeService;

    private UserMapper userMapper;

    /**
     * 看板卡片
     */
    @PostMapping("/card")
    public ApiResponse<List<DashboardCardVO>> card() {
        int userId = StpUtil.getLoginIdAsInt();

        List<DashboardCardVO> dashboardCardList = new ArrayList<>();
        // 获取今年已过
        dashboardCardList.add(getYearPassedCard());

        try {
            // 同步leetcode信息
            UserEntity userEntity = userMapper.selectById(userId);

            // 异步获取今日提交次数
            CompletableFuture<Integer> submissionCountFuture = CompletableFuture.supplyAsync(() ->
                    leetcodeService.getTodaySubmissionCount(userEntity.getLeetcodeAcct())
            );

            boolean finish = leetcodeService.checkToday(userEntity, false);
            DashboardCardVO dashboardCardVO = new DashboardCardVO();
            dashboardCardVO.setIcon("devicon:leetcode");
            dashboardCardVO.setTitle("每日一题");
            if (finish) {
                dashboardCardVO.setValue("已完成");
            } else {
                dashboardCardVO.setValue("未完成");
                dashboardCardVO.setValueColor("red");
            }

            dashboardCardVO.setTotalTitle("今日提交");

            // 获取异步结果，超时时间设置为5秒
            Integer submissionCount = 0;
            try {
                submissionCount = submissionCountFuture.get(3, TimeUnit.SECONDS);
                dashboardCardVO.setTotalValue(String.valueOf(submissionCount));
            } catch (Exception e) {
                dashboardCardVO.setTotalValue("获取失败");
                log.error("异步获取 LeetCode 今日提交次数失败", e);
            }

            dashboardCardList.add(dashboardCardVO);
        } catch (Exception e) {
            log.error("获取看板卡片失败", e);
        }

        return ApiResponse.success(dashboardCardList);
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
