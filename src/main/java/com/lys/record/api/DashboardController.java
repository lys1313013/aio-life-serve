package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.lys.core.resq.ApiResponse;
import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.record.service.ILeetcodeService;
import com.lys.sso.mapper.UserMapper;
import com.lys.sso.pojo.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 看板
 *
 * @author Lys
 * @date 2025/04/13 13:56
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
    private ILeetcodeService leetcodeService;

    private UserMapper userMapper;


    @PostMapping("/card")
    public ApiResponse<List<DashboardCardVO>> card() {
        int userId = StpUtil.getLoginIdAsInt();

        // 同步leetcode信息
        UserEntity userEntity = userMapper.selectById(userId);
        leetcodeService.syncLeetcodeInfo(userEntity);

        List<DashboardCardVO> dashboardCard = leetcodeService.getDashboardCard(userId);
        return ApiResponse.success(dashboardCard);
    }

}
