package top.aiolife.system.pojo.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统配置响应 VO
 *
 * @author Ethan
 * @date 2026/07/19
 */
@Getter
@Setter
public class SystemConfigVO {

    private String configKey;

    private String configValue;

    /**
     * STRING / JSON / BOOLEAN / NUMBER
     */
    private String configType;

    private String description;

    private String updateTime;
}
