package top.aiolife.sso.pojo.req;

import lombok.Data;

/**
 * API Key 生成请求参数
 *
 * @author Lys
 * @date 2026/03/09
 */
@Data
public class ApiKeyGenerateReq {
    /**
     * 备注
     */
    private String remark;

    /**
     * 过期天数
     */
    private Integer expireDays;
}
