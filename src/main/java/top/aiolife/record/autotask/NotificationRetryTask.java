package top.aiolife.record.autotask;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.aiolife.record.service.FeishuNotificationService;
import top.aiolife.record.util.RedisUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NotificationRetryTask {

    private final FeishuNotificationService notificationService;
    private final RedisUtil redisUtil;

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void retryFeishuNotifications() {
        String lockKey = "notification:feishu:retry:lock";
        String lockValue = UUID.randomUUID().toString();
        if (!redisUtil.setIfAbsent(lockKey, lockValue, 10, TimeUnit.MINUTES)) {
            return;
        }
        try {
            notificationService.retryPending();
        } finally {
            redisUtil.unlock(lockKey, lockValue);
        }
    }
}
