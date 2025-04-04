package com.lys.sso.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lys.mapper.UserMapper;
import com.lys.core.resq.ApiResponse;
import com.lys.sso.pojo.entity.UserEntity;
import com.lys.sso.pojo.req.LoginReq;
import com.lys.sso.pojo.vo.UserInfoVO;
import com.lys.sso.pojo.vo.UserLoginVO;
import com.lys.util.Md5Util;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/4/3
 */
@RestController
@AllArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    /**
     * 盐值（待抽取）
     */
    @Value("${sa-token.salt}")
    public static String salt = "test";

    /**
     * 登录
     *
     * @author Lys
     * @date 2025/4/4
     */
    @PostMapping("/auth/login")
    public ApiResponse<UserLoginVO> login(@RequestBody LoginReq loginReq) {
        LambdaQueryWrapper<UserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserEntity::getUsername, loginReq.getUsername());
        // 密码加盐
        String password = loginReq.getPassword();
        String encryptedPassword = Md5Util.encryptPassword(password, salt);

        lambdaQueryWrapper.eq(UserEntity::getPassword, encryptedPassword);

        UserEntity userEntity = userMapper.selectOne(lambdaQueryWrapper);
        if (userEntity == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        StpUtil.login(userEntity.getId());
        String token = StpUtil.getTokenValue();

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setId(userEntity.getId());
        userLoginVO.setRealName(userEntity.getNickname());
        userLoginVO.setUsername(userEntity.getNickname());
        userLoginVO.setRoles(List.of("super"));
        userLoginVO.setAccessToken(token);
        return ApiResponse.success(userLoginVO);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user/info")
    public ApiResponse<UserInfoVO> info() {
        int id = StpUtil.getLoginIdAsInt();
        UserEntity userEntity = userMapper.selectById(id);
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(userEntity.getId());
        userInfoVO.setRealName(userEntity.getNickname());
        userInfoVO.setUsername(userEntity.getNickname());
        userInfoVO.setRoles(List.of("super"));
        return ApiResponse.success(userInfoVO);
    }

    @GetMapping("/auth/codes")
    public ApiResponse<Void> codes() {
        Map<String, Object> data = new HashMap<>();
        data.put("data", new String[]{"AC_100100", "AC_100110", "AC_100120", "AC_100010"});
        Map<String, Object> map = new HashMap<>();
        map.put("rscode", "0");
        return ApiResponse.success();
    }


    @PostMapping("/auth/logout")
    public ApiResponse<Map<String, Object>> logout() {
        StpUtil.logout();
        return ApiResponse.success();
    }
}
