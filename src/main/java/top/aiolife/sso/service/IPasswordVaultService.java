package top.aiolife.sso.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.sso.pojo.entity.PasswordVaultEntity;

import java.util.List;

/**
 * 密码库 Service
 *
 * @author Lys
 * @date 2026/04/28
 */
public interface IPasswordVaultService extends IService<PasswordVaultEntity> {

    /**
     * 获取用户的所有密码
     *
     * @param userId 用户ID
     * @return 密码列表
     */
    List<PasswordVaultEntity> listByUserId(Long userId);

    /**
     * 获取密码详情
     *
     * @param id 密码ID
     * @param userId 用户ID
     * @return 密码实体
     */
    PasswordVaultEntity getByIdAndUserId(Long id, Long userId);

    /**
     * 新增密码
     *
     * @param entity 密码实体
     * @param userId 用户ID
     */
    void savePassword(PasswordVaultEntity entity, Long userId);

    /**
     * 更新密码
     *
     * @param entity 密码实体
     * @param id 密码ID
     * @param userId 用户ID
     */
    void updatePassword(PasswordVaultEntity entity, Long id, Long userId);

    /**
     * 删除密码
     *
     * @param id 密码ID
     * @param userId 用户ID
     */
    void deletePassword(Long id, Long userId);

    /**
     * 获取分类列表
     *
     * @param userId 用户ID
     * @return 分类列表
     */
    List<String> listCategories(Long userId);
}