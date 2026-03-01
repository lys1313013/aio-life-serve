package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.aiolife.record.mapper.UserBindMapper;
import top.aiolife.record.pojo.entity.UserBindEntity;
import top.aiolife.record.service.IUserBindService;

import java.util.List;

/**
 * 用户绑定服务实现类
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBindEntity> implements IUserBindService {

    @Override
    public List<UserBindEntity> getBindsByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<UserBindEntity>()
                .eq(UserBindEntity::getUserId, userId));
    }

    @Override
    public UserBindEntity getBindByUserIdAndPlatform(Long userId, String platform) {
        return this.getOne(new LambdaQueryWrapper<UserBindEntity>()
                .eq(UserBindEntity::getUserId, userId)
                .eq(UserBindEntity::getPlatform, platform));
    }
}
