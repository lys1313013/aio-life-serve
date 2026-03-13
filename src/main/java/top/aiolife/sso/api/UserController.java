package top.aiolife.sso.api;

import cn.dev33.satoken.stp.StpUtil;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.constant.ResponseCodeConst;
import top.aiolife.sso.pojo.entity.UserEntity;
import top.aiolife.sso.pojo.req.ChangePasswordReq;
import top.aiolife.sso.pojo.req.LoginReq;
import top.aiolife.sso.pojo.req.UpdateUserReq;
import top.aiolife.sso.pojo.vo.UserBasicInfoVO;
import top.aiolife.sso.pojo.vo.UserInfoVO;
import top.aiolife.sso.pojo.vo.UserLoginVO;
import top.aiolife.core.util.MinioUtil;
import top.aiolife.sso.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    private final MinioUtil minioUtil;

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
     * 修改密码
     */
    @PostMapping("/auth/change-password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordReq changePasswordReq) {
        long id = StpUtil.getLoginIdAsLong();
        userService.changePassword(id, changePasswordReq);
        return ApiResponse.success();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user/info")
    public ApiResponse<UserInfoVO> info() {
        long id = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(userService.getUserInfo(id));
    }

    /**
     * 获取用户基本信息
     */
    @GetMapping("/user/{id}/basic")
    public ApiResponse<UserBasicInfoVO> basicInfo(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserBasicInfo(id));
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

    /**
     * 普通更新用户信息
     */
    @PutMapping("/users")
    public ApiResponse<Void> modify(@RequestBody UpdateUserReq req) {
        long id = StpUtil.getLoginIdAsLong();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setNickname(req.getNickname());
        userEntity.setIntroduction(req.getIntroduction());
        userEntity.setAvatar(req.getAvatar());
        userService.updateUser(userEntity);
        return ApiResponse.success();
    }

    /**
     * 上传头像文件
     */
    @PostMapping("/users/avatar/upload")
    public ApiResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // 生成唯一文件名
            String fileName = minioUtil.generateUniqueFileName(file.getOriginalFilename());
            String objectName = "images/avatar/" + fileName;
            // 上传到 minio，使用 images/avatar 前缀
            minioUtil.uploadFile(file, objectName);
            
            // 构建完整的文件访问URL
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String imageUrl = baseUrl + "/file/preview?fileName=" + objectName;
                    
            return ApiResponse.success(imageUrl);
        } catch (Exception e) {
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, "上传失败: " + e.getMessage());
        }
    }
}
