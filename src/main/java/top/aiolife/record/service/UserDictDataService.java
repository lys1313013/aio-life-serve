package top.aiolife.record.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.record.pojo.entity.entity.UserDictDataEntity;

import java.util.List;

/**
 * 用户字典数据服务接口
 *
 * @author Lys
 */
public interface UserDictDataService extends IService<UserDictDataEntity> {

    /**
     * 获取用户可见的字典数据（合并基础值和个人配置）
     */
    List<UserDictDataEntity> listUserVisibleDictData(Long userId, String dictType);

    /**
     * 创建字典数据
     */
    void createDictData(UserDictDataEntity entity, Long userId);

    /**
     * 更新字典数据（支持覆盖基础值）
     */
    void updateDictData(Long id, UserDictDataEntity entity, Long userId);

    /**
     * 删除/隐藏字典数据（支持隐藏基础值）
     */
    void deleteDictData(Long id, Long userId);
}
