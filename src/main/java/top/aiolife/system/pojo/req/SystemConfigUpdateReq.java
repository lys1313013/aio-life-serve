package top.aiolife.system.pojo.req;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统配置更新请求
 *
 * @author Ethan
 * @date 2026/07/19
 */
@Getter
@Setter
public class SystemConfigUpdateReq {

    /**
     * 新值（JSON 或纯文本）
     */
    private String configValue;
}
