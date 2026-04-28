package top.aiolife.sso.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.aiolife.sso.mapper.PasswordVaultMapper;
import top.aiolife.sso.pojo.entity.PasswordVaultEntity;
import top.aiolife.sso.service.PasswordVaultService;

import java.util.List;

/**
 * 密码库 Service 实现
 *
 * @author Lys
 * @date 2026-04-28
 */
@Service
@RequiredArgsConstructor
public class PasswordVaultServiceImpl implements PasswordVaultService {

    private final PasswordVaultMapper passwordVaultMapper;

    @Override
    public List<PasswordVaultEntity> listByUserId(Long userId) {
        return passwordVaultMapper.selectList(
                new LambdaQueryWrapper<PasswordVaultEntity>()
                        .eq(PasswordVaultEntity::getUserId, userId)
                        .orderByDesc(PasswordVaultEntity::getFavorite)
                        .orderByDesc(PasswordVaultEntity::getCreateTime)
        );
    }

    @Override
    public PasswordVaultEntity getById(Long id, Long userId) {
        return passwordVaultMapper.selectOne(
                new LambdaQueryWrapper<PasswordVaultEntity>()
                        .eq(PasswordVaultEntity::getId, id)
                        .eq(PasswordVaultEntity::getUserId, userId)
        );
    }

    @Override
    public PasswordVaultEntity create(PasswordVaultEntity entity, Long userId) {
        entity.fillCreateCommonField(userId);
        entity.setFavorite(false);
        passwordVaultMapper.insert(entity);
        return entity;
    }

    @Override
    public PasswordVaultEntity update(PasswordVaultEntity entity, Long userId) {
        entity.setId(null);
        entity.setUserId(null);
        entity.setCreateUser(null);
        entity.setCreateTime(null);
        entity.fillUpdateCommonField(userId);
        passwordVaultMapper.updateById(entity);
        return getById(entity.getId(), userId);
    }

    @Override
    public void delete(Long id, Long userId) {
        passwordVaultMapper.delete(
                new LambdaQueryWrapper<PasswordVaultEntity>()
                        .eq(PasswordVaultEntity::getId, id)
                        .eq(PasswordVaultEntity::getUserId, userId)
        );
    }

    @Override
    public List<String> getCategories(Long userId) {
        List<PasswordVaultEntity> list = listByUserId(userId);
        return list.stream()
                .map(PasswordVaultEntity::getCategory)
                .filter(StringUtils::hasText)
                .distinct()
                .sorted()
                .toList();
    }
}