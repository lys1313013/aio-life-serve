package top.aiolife.record.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.record.pojo.entity.UserBindEntity;

import java.util.List;

/**
 * 用户绑定服务接口
 */
public interface IUserBindService extends IService<UserBindEntity> {

    /**
     * 获取用户的所有绑定信息
     * @param userId 用户ID
     * @return 绑定列表
     */
    List<UserBindEntity> getBindsByUserId(Long userId);

    /**
     * 获取用户指定平台的绑定信息
     * @param userId 用户ID
     * @param platform 平台类型
     * @return 绑定信息
     */
    UserBindEntity getBindByUserIdAndPlatform(Long userId, String platform);
}
