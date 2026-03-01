package top.aiolife.sso.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import top.aiolife.sso.pojo.entity.UserEntity;
import top.aiolife.sso.service.IUserService;
import top.aiolife.sso.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserMapper userMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本项目暂时没有权限码控制，只有角色控制，因此返回空集合
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 1. 获取用户ID
        long userId = Long.parseLong(loginId.toString());
        
        // 2. 查询用户信息
        UserEntity userEntity = userMapper.selectById(userId);
        
        // 3. 返回角色列表
        if (userEntity != null && StringUtils.hasText(userEntity.getRole())) {
            return Arrays.asList(userEntity.getRole().split(","));
        }
        
        // 默认返回 user 角色
        return Collections.singletonList("user");
    }
}
