package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.lys.core.resq.ApiResponse;
import com.lys.record.service.ILeetcodeService;
import jakarta.mail.MessagingException;
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

    /**
     * 没啥地方使用，只是用来手动触发
     */
    @GetMapping("/notifyTodayQuestion")
    public ApiResponse<Void> notifyTodayQuestion() throws MessagingException {
        StpUtil.getLoginIdAsInt();
        leetcodeService.notifyTodayQuestion();
        return ApiResponse.success();
    }
}
