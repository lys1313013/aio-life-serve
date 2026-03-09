package top.aiolife.sso.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.aiolife.sso.mapper.ApiKeyLogMapper;
import top.aiolife.sso.pojo.entity.ApiKeyLogEntity;
import top.aiolife.sso.service.IApiKeyLogService;

import java.time.LocalDateTime;

/**
 * API Key 调用日志 Service 实现类
 *
 * @author Lys
 * @date 2026/03/09
 */
@Service
public class ApiKeyLogServiceImpl extends ServiceImpl<ApiKeyLogMapper, ApiKeyLogEntity> implements IApiKeyLogService {

    @Override
    public void log(Long apiKeyId, String path, String method, Integer status, String ip) {
        ApiKeyLogEntity logEntity = new ApiKeyLogEntity();
        logEntity.setApiKeyId(apiKeyId);
        logEntity.setRequestPath(path);
        logEntity.setRequestMethod(method);
        logEntity.setResponseStatus(status);
        logEntity.setClientIp(ip);
        logEntity.setCreateTime(LocalDateTime.now());
        this.save(logEntity);
    }
}
