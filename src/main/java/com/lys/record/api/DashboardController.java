package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.lys.core.resq.ApiResponse;
import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.record.pojo.vo.StaticVO;
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

        List<DashboardCardVO> dashboardCard = new ArrayList<>();
        try {
            // 同步leetcode信息
            UserEntity userEntity = userMapper.selectById(userId);
            leetcodeService.syncLeetcodeInfo(userEntity);
            dashboardCard.addAll(leetcodeService.getDashboardCard(userId));
        } catch (Exception e) {
            log.error("获取看板卡片失败", e);
        }
        // 今年已过
        // 计算今年已过的天数
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfYear = LocalDate.of(today.getYear(), 1, 1);
        long daysPassed = ChronoUnit.DAYS.between(firstDayOfYear, today) + 1;
        DashboardCardVO dashboardCardVO = new DashboardCardVO();
        dashboardCardVO.setTitle("今年已过");
        dashboardCardVO.setValue((int) daysPassed);
        dashboardCardVO.setTotalTitle("剩余天数");
        dashboardCardVO.setTotalValue(today.lengthOfYear() - (int) daysPassed);
        dashboardCard.add(dashboardCardVO);
        return ApiResponse.success(dashboardCard);
    }

    /**
     * 统计信息
     */
    @PostMapping("/static")
    public ApiResponse<StaticVO> staticInfo() {
        int userId = StpUtil.getLoginIdAsInt();

        List<StaticVO> voList = new ArrayList<>();
        StaticVO staticVO = new StaticVO();
        staticVO.setName("今年已过");

        // 计算今年已过的天数
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfYear = LocalDate.of(today.getYear(), 1, 1);
        long daysPassed = ChronoUnit.DAYS.between(firstDayOfYear, today) + 1;
        staticVO.setValue((int) daysPassed);

        voList.add(staticVO);
        return ApiResponse.success(staticVO);
    }
}
