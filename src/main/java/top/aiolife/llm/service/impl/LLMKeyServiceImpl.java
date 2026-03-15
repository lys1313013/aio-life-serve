package top.aiolife.llm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.aiolife.llm.mapper.LLMKeyMapper;
import top.aiolife.llm.pojo.entity.LLMKeyEntity;
import top.aiolife.llm.service.LLMKeyService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class LLMKeyServiceImpl implements LLMKeyService {

    private final LLMKeyMapper llmKeyMapper;

    @Override
    public void saveLLMKey(LLMKeyEntity llmKeyEntity) {
        try {
            llmKeyEntity.setCreateTime(LocalDateTime.now());
            llmKeyEntity.setUpdateTime(LocalDateTime.now());

            // 如果设置为默认，先将其他配置设为非默认
            if (llmKeyEntity.getIsDefault() != null && llmKeyEntity.getIsDefault() == 1) {
                LambdaUpdateWrapper<LLMKeyEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(LLMKeyEntity::getUserId, llmKeyEntity.getUserId())
                        .set(LLMKeyEntity::getIsDefault, 0);
                llmKeyMapper.update(null, updateWrapper);
            }

            llmKeyMapper.insert(llmKeyEntity);
        } catch (Exception e) {
            log.error("Failed to save LLM key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save LLM key", e);
        }
    }

    @Override
    public void updateLLMKey(LLMKeyEntity llmKeyEntity) {
        try {
            llmKeyEntity.setUpdateTime(LocalDateTime.now());

            // 如果设置为默认，先将其他配置设为非默认
            if (llmKeyEntity.getIsDefault() != null && llmKeyEntity.getIsDefault() == 1) {
                LambdaUpdateWrapper<LLMKeyEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(LLMKeyEntity::getUserId, llmKeyEntity.getUserId())
                        .set(LLMKeyEntity::getIsDefault, 0);
                llmKeyMapper.update(null, updateWrapper);
            }

            llmKeyMapper.updateById(llmKeyEntity);
        } catch (Exception e) {
            log.error("Failed to update LLM key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update LLM key", e);
        }
    }

    @Override
    public void deleteLLMKey(String id, Long userId) {
        try {
            LambdaQueryWrapper<LLMKeyEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LLMKeyEntity::getId, id)
                    .eq(LLMKeyEntity::getUserId, userId);
            llmKeyMapper.delete(queryWrapper);
        } catch (Exception e) {
            log.error("Failed to delete LLM key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete LLM key", e);
        }
    }

    @Override
    public List<LLMKeyEntity> getLLMKeyList(Long userId) {
        try {
            return llmKeyMapper.selectByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to get LLM key list: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get LLM key list", e);
        }
    }

    @Override
    public LLMKeyEntity getDefaultLLMKey(Long userId) {
        try {
            return llmKeyMapper.selectDefaultByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to get default LLM key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get default LLM key", e);
        }
    }

    @Override
    public void setDefaultLLMKey(String id, Long userId) {
        try {
            // 先将所有配置设为非默认
            LambdaUpdateWrapper<LLMKeyEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(LLMKeyEntity::getUserId, userId)
                    .set(LLMKeyEntity::getIsDefault, 0);
            llmKeyMapper.update(null, updateWrapper);

            // 将指定配置设为默认
            LLMKeyEntity llmKeyEntity = new LLMKeyEntity();
            llmKeyEntity.setId(id);
            llmKeyEntity.setIsDefault(1);
            llmKeyEntity.setUpdateTime(LocalDateTime.now());
            llmKeyMapper.updateById(llmKeyEntity);
        } catch (Exception e) {
            log.error("Failed to set default LLM key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to set default LLM key", e);
        }
    }
}
