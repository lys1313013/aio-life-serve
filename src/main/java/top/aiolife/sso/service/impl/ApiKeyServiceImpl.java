package top.aiolife.sso.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.aiolife.sso.mapper.ApiKeyMapper;
import top.aiolife.sso.pojo.entity.ApiKeyEntity;
import top.aiolife.sso.service.IApiKeyService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API Key Service 实现类
 *
 * @author Lys
 * @date 2026/03/09
 */
@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKeyEntity> implements IApiKeyService {

    @Override
    public ApiKeyEntity generateApiKey(Long userId, String remark, Integer expireDays) {
        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setUserId(userId);
        entity.setRemark(remark);
        // 生成前缀为 ak- 的 32 位随机字符串
        entity.setApiKey("ak-" + IdUtil.fastSimpleUUID());
        if (expireDays != null && expireDays > 0) {
            entity.setExpiredAt(LocalDateTime.now().plusDays(expireDays));
        }
        entity.setCreateUser(userId);
        entity.setUpdateUser(userId);
        this.save(entity);
        return entity;
    }

    @Override
    public List<ApiKeyEntity> listByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<ApiKeyEntity>()
                .eq(ApiKeyEntity::getUserId, userId)
                .orderByDesc(ApiKeyEntity::getCreateTime));
    }

    @Override
    public ApiKeyEntity getByApiKey(String apiKey) {
        return this.getOne(new LambdaQueryWrapper<ApiKeyEntity>()
                .eq(ApiKeyEntity::getApiKey, apiKey));
    }
}
