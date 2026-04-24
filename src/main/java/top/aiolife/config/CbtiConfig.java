package top.aiolife.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * CBTI 配置类
 *
 * @author Ethan
 * @date 2026/04/18
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "aio.life.serve.cbti")
public class CbtiConfig {

    private boolean enabled = true;

    private boolean initOnStartup = true;

    private String bucketName;

    private String objectPrefix = "images/cbti/characters/";

    private String charactersDir;
}

