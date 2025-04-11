package com.lys.record.autotask;

import com.lys.record.service.ILeetcodeService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/11 23:53
 */
@Service
public class AutoTask {

    @Resource
    private ILeetcodeService leetcodeService;

    @Scheduled(cron = "${aio.life.serve.corn:0 30 23 * * ?}")
    public void leetcodeCheck() {
        leetcodeService.check();
    }
}
