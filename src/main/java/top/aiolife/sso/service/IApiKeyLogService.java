package top.aiolife.sso.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.sso.pojo.entity.ApiKeyLogEntity;

/**
 * API Key 调用日志 Service
 *
 * @author Lys
 * @date 2026/03/09
 */
public interface IApiKeyLogService extends IService<ApiKeyLogEntity> {
    
    /**
     * 记录调用日志
     *
     * @param apiKeyId API Key ID
     * @param path 请求路径
     * @param method 请求方法
     * @param status 响应状态码
     * @param ip 客户端IP
     */
    void log(Long apiKeyId, String path, String method, Integer status, String ip);
}
