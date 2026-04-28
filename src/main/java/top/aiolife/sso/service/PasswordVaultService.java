package top.aiolife.sso.service;

import top.aiolife.core.resq.PageResp;
import top.aiolife.sso.pojo.entity.PasswordVaultEntity;

import java.util.List;

/**
 * 密码库 Service
 *
 * @author Lys
 * @date 2026-04-28
 */
public interface PasswordVaultService {

    /**
     * 查询密码列表
     */
    List<PasswordVaultEntity> listByUserId(Long userId);

    /**
     * 获取单条密码
     */
    PasswordVaultEntity getById(Long id, Long userId);

    /**
     * 创建密码
     */
    PasswordVaultEntity create(PasswordVaultEntity entity, Long userId);

    /**
     * 更新密码
     */
    PasswordVaultEntity update(PasswordVaultEntity entity, Long userId);

    /**
     * 删除密码
     */
    void delete(Long id, Long userId);

    /**
     * 获取分类列表
     */
    List<String> getCategories(Long userId);
}