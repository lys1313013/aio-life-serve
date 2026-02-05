package top.aiolife.sso.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import top.aiolife.sso.mapper.LoginLogMapper;
import top.aiolife.sso.mapper.UserMapper;
import top.aiolife.sso.pojo.entity.LoginLogEntity;
import top.aiolife.sso.pojo.entity.UserEntity;
import top.aiolife.sso.pojo.req.LoginReq;
import top.aiolife.sso.pojo.vo.UserInfoVO;
import top.aiolife.sso.pojo.vo.UserLoginVO;
import top.aiolife.sso.service.IUserService;
import top.aiolife.sso.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
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

    @Override
    public UserLoginVO login(LoginReq loginReq, String ip) {
        LambdaQueryWrapper<UserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserEntity::getUsername, loginReq.getUsername());

        UserEntity userEntity = userMapper.selectOne(lambdaQueryWrapper);

        // 记录登录日志
        LoginLogEntity loginLogEntity = new LoginLogEntity();
        loginLogEntity.setUsername(loginReq.getUsername());
        loginLogEntity.setIpAddress(ip);

        if (userEntity == null) {
            loginLogEntity.setPassword(loginReq.getPassword());
            loginLogMapper.insert(loginLogEntity);
            throw new RuntimeException("用户名或密码错误");
        }

        // 校验密码
        String encryptedPassword = PasswordUtil.encryptPassword(loginReq.getPassword(), userEntity.getPasswordSalt());
        if (!userEntity.getPassword().equals(encryptedPassword)) {
            loginLogEntity.setPassword(loginReq.getPassword());
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
        userInfoVO.setNickname(userEntity.getNickname());
        userInfoVO.setAvatar(userEntity.getAvatar());
        userInfoVO.setEmail(userEntity.getEmail());
        userInfoVO.setRoles(List.of("super"));
        userInfoVO.setGithubUsername(userEntity.getGithubUsername());
        userInfoVO.setGithubToken(userEntity.getGithubToken());
        userInfoVO.setLeetcodeAcct(userEntity.getLeetcodeAcct());
        userInfoVO.setShanbayAcct(userEntity.getShanbayAcct());
        return userInfoVO;
    }

    @Override
    public void updateUser(UserEntity userEntity) {
        userMapper.updateById(userEntity);
    }
}
