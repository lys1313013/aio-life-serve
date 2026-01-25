package top.aiolife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/04 13:53
 */
@EnableCaching
@EnableScheduling // 添加此注解启用定时任务
@SpringBootApplication
public class AioLifeMain {
    public static void main(String[] args) {
        SpringApplication.run(AioLifeMain.class, args);
    }
}
