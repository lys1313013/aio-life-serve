package top.aiolife.sso.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.sso.pojo.req.RegisterReq;
import top.aiolife.sso.pojo.req.ResetPasswordReq;
import top.aiolife.sso.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证控制器
 *
 * @author Lys
 * @date 2026/03/01
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;

    /**
     * 发送注册验证码
     *
     * @param email 邮箱
     */
    @GetMapping("/sendEmailCode")
    public ApiResponse<Void> sendEmailCode(@RequestParam String email, HttpServletRequest request) {
        String ip = getIp(request);
        userService.sendRegisterCode(email, ip);
        return ApiResponse.success();
    }

    /**
     * 发送重置密码验证码
     *
     * @param email 邮箱
     */
    @GetMapping("/sendResetPasswordCode")
    public ApiResponse<Void> sendResetPasswordCode(@RequestParam String email, HttpServletRequest request) {
        String ip = getIp(request);
        userService.sendResetPasswordCode(email, ip);
        return ApiResponse.success();
    }

    /**
     * 获取客户端IP地址
     */
    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 重置密码
     *
     * @param resetPasswordReq 重置密码参数
     */
    @PostMapping("/resetPassword")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordReq resetPasswordReq) {
        userService.resetPassword(resetPasswordReq);
        return ApiResponse.success();
    }

    /**
     * 注册
     *
     * @param registerReq 注册参数
     */
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterReq registerReq) {
        userService.register(registerReq);
        return ApiResponse.success();
    }
}
