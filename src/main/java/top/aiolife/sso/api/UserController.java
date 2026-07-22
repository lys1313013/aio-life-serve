package top.aiolife.sso.api;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aiolife.core.constant.ResponseCodeConst;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.record.pojo.entity.FileEntity;
import top.aiolife.record.service.IFileService;
import top.aiolife.sso.pojo.entity.UserEntity;
import top.aiolife.sso.pojo.req.ChangePasswordReq;
import top.aiolife.sso.pojo.req.LoginReq;
import top.aiolife.sso.pojo.req.UpdateUserReq;
import top.aiolife.sso.pojo.vo.UserBasicInfoVO;
import top.aiolife.sso.pojo.vo.UserInfoVO;
import top.aiolife.sso.pojo.vo.UserLoginVO;
import top.aiolife.sso.service.IUserService;

import java.util.HashMap;
import java.util.Map;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/4/3
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final IFileService fileService;

    @Value("${aio.life.server.base-url}")
    private String serveBaseUrl;

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
    @GetMapping("/auth/info")
    public ApiResponse<UserInfoVO> authInfo() {
        return info();
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
            String fileName = file.getOriginalFilename();
            String extension = fileName != null && fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
            String objectName = StpUtil.getLoginIdAsLong() + "/avatar/" + java.util.UUID.randomUUID().toString() + extension;
            String bucketName = "aiolife";
            
            // 头像必须设置为公开可见 (isPublic = 1)
            FileEntity fileEntity = fileService.uploadAndSave(file, "avatar", bucketName, objectName, 1);
            
            // 返回文件预览URL (前端也可以直接用这个URL)
            String imageUrl = serveBaseUrl + "/file/preview/" + fileEntity.getId();
                    
            return ApiResponse.success(imageUrl);
        } catch (Exception e) {
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, "上传失败: " + e.getMessage());
        }
    }
}
