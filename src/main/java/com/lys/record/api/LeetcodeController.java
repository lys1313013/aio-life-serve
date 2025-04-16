package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.lys.core.resq.ApiResponse;
import com.lys.record.service.ILeetcodeService;
import com.lys.sso.mapper.UserMapper;
import com.lys.sso.pojo.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/leetcode")
public class LeetcodeController {

    private ILeetcodeService leetcodeService;

    private UserMapper userMapper;

    @GetMapping("/heatmap")
    public ApiResponse<Void> fetchAndSaveUserCalendar() {
        int userId = StpUtil.getLoginIdAsInt();
        UserEntity userEntity = userMapper.selectById(userId);
        leetcodeService.syncLeetcodeInfo(userEntity);
        return ApiResponse.success();
    }
}
