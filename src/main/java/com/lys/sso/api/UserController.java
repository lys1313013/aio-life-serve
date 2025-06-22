package com.lys.sso.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lys.core.resq.ApiResponse;
import com.lys.sso.mapper.LoginLogMapper;
import com.lys.sso.mapper.UserMapper;
import com.lys.sso.pojo.entity.LoginLogEntity;
import com.lys.sso.pojo.entity.UserEntity;
import com.lys.sso.pojo.req.LoginReq;
import com.lys.sso.pojo.vo.UserInfoVO;
import com.lys.sso.pojo.vo.UserLoginVO;
import com.lys.sso.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    private final LoginLogMapper loginLogMapper;

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
    public ApiResponse<UserLoginVO> login(@RequestBody LoginReq loginReq, HttpServletRequest request) {
        LambdaQueryWrapper<UserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserEntity::getUsername, loginReq.getUsername());
        // 密码加盐
        String password = loginReq.getPassword();
        String encryptedPassword = PasswordUtil.encryptPassword(password, salt);

        lambdaQueryWrapper.eq(UserEntity::getPassword, encryptedPassword);

        // 记录登录日志
        LoginLogEntity loginLogEntity = new LoginLogEntity();
        loginLogEntity.setUsername(loginReq.getUsername());
        String ip = getIp(request);
        // 可以将ipAddress记录到loginLogEntity中
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
        userInfoVO.setAvatar(userEntity.getAvatar());
        userInfoVO.setEmail(userEntity.getEmail());
        userInfoVO.setRoles(List.of("super"))       ;
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

    /**
     * 从请求头中获取IP
     */
    public String getIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
