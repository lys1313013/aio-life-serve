package com.lys.record.autotask;

import com.lys.record.service.ILeetcodeService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
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

    @Scheduled(cron = "${aio.life.serve.corn.leetcodeCheck:0 30 23 * * ?}")
    public void leetcodeCheck() {
        leetcodeService.check();
    }

    @Scheduled(cron = "${aio.life.serve.corn.notifyTodayQuestion:0 0 7 * * ?}")
    public void notifyTodayQuestion() throws MessagingException {
        leetcodeService.notifyTodayQuestion();
    }
}
