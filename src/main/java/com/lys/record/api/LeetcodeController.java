package com.lys.record.api;

import com.lys.core.resq.ApiResponse;
import com.lys.record.service.ILeetcodeService;
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

    @GetMapping("/heatmap")
    public ApiResponse<Void> fetchAndSaveUserCalendar() {
        leetcodeService.check();
        return ApiResponse.success();
    }
}
