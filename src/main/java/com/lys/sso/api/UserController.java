package com.lys.sso.api;

import cn.dev33.satoken.stp.StpUtil;
import com.lys.core.resq.ApiResponse;
import com.lys.sso.pojo.req.LoginReq;
import com.lys.sso.pojo.vo.UserInfoVO;
import com.lys.sso.pojo.vo.UserLoginVO;
import com.lys.sso.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

    private final IUserService userService;

    /**
     * 登录
     *
     * @author Lys
     * @date 2025/4/4
     */
    @PostMapping("/auth/login")
    public ApiResponse<UserLoginVO> login(@RequestBody LoginReq loginReq, HttpServletRequest request) {
        String ip = getIp(request);
        return ApiResponse.success(userService.login(loginReq, ip));
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user/info")
    public ApiResponse<UserInfoVO> info() {
        int id = StpUtil.getLoginIdAsInt();
        return ApiResponse.success(userService.getUserInfo(id));
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
