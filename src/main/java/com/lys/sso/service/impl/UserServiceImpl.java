package com.lys.sso.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lys.sso.mapper.LoginLogMapper;
import com.lys.sso.mapper.UserMapper;
import com.lys.sso.pojo.entity.LoginLogEntity;
import com.lys.sso.pojo.entity.UserEntity;
import com.lys.sso.pojo.req.LoginReq;
import com.lys.sso.pojo.vo.UserInfoVO;
import com.lys.sso.pojo.vo.UserLoginVO;
import com.lys.sso.service.IUserService;
import com.lys.sso.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 *
 * @author Lys
 * @date 2025/12/06 23:58
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final LoginLogMapper loginLogMapper;

    @Value("${sa-token.salt}")
    private String salt;

    @Override
    public UserLoginVO login(LoginReq loginReq, String ip) {
        LambdaQueryWrapper<UserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserEntity::getUsername, loginReq.getUsername());
        // 密码加盐
        String password = loginReq.getPassword();
        String encryptedPassword = PasswordUtil.encryptPassword(password, salt);

        lambdaQueryWrapper.eq(UserEntity::getPassword, encryptedPassword);

        // 记录登录日志
        LoginLogEntity loginLogEntity = new LoginLogEntity();
        loginLogEntity.setUsername(loginReq.getUsername());
        loginLogEntity.setIpAddress(ip);

        UserEntity userEntity = userMapper.selectOne(lambdaQueryWrapper);
        if (userEntity == null) {
            loginLogEntity.setPassword(password);
            loginLogMapper.insert(loginLogEntity);
            throw new RuntimeException("用户名或密码错误");
        }
        loginLogEntity.setUserId(userEntity.getId());
        loginLogMapper.insert(loginLogEntity);
        StpUtil.login(userEntity.getId());
        String token = StpUtil.getTokenValue();

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setId(userEntity.getId());
        userLoginVO.setRealName(userEntity.getNickname());
        userLoginVO.setUsername(userEntity.getNickname());
        userLoginVO.setRoles(List.of("super"));
        userLoginVO.setAccessToken(token);
        return userLoginVO;
    }

    @Override
    @Cacheable(value = "userInfo", key = "#userId")
    public UserInfoVO getUserInfo(int userId) {
        UserEntity userEntity = userMapper.selectById(userId);
        if (userEntity == null) {
            return null;
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(userEntity.getId());
        userInfoVO.setRealName(userEntity.getNickname());
        userInfoVO.setUsername(userEntity.getNickname());
        userInfoVO.setAvatar(userEntity.getAvatar());
        userInfoVO.setEmail(userEntity.getEmail());
        userInfoVO.setRoles(List.of("super"));
        userInfoVO.setGithubUsername(userEntity.getGithubUsername());
        userInfoVO.setGithubToken(userEntity.getGithubToken());
        return userInfoVO;
    }
}
