package top.aiolife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/04 13:53
 */
@EnableCaching
@SpringBootApplication
public class AioLifeMain {
    public static void main(String[] args) {
        SpringApplication.run(AioLifeMain.class, args);
    }
}
