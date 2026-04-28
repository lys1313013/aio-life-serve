package top.aiolife.sso.api;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.sso.pojo.entity.PasswordVaultEntity;
import top.aiolife.sso.service.PasswordVaultService;

import java.util.List;

/**
 * 密码库 Controller
 *
 * @author Lys
 * @date 2026-04-28
 */
@Slf4j
@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordVaultController {

    private final PasswordVaultService passwordVaultService;

    /**
     * 查询密码列表
     */
    @GetMapping("/list")
    public ApiResponse<List<PasswordVaultEntity>> list() {
        Long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(passwordVaultService.listByUserId(userId));
    }

    /**
     * 获取单条密码
     */
    @GetMapping("/{id}")
    public ApiResponse<PasswordVaultEntity> get(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(passwordVaultService.getById(id, userId));
    }

    /**
     * 创建密码
     */
    @PostMapping
    public ApiResponse<PasswordVaultEntity> create(@RequestBody PasswordVaultEntity entity) {
        Long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(passwordVaultService.create(entity, userId));
    }

    /**
     * 更新密码
     */
    @PutMapping("/{id}")
    public ApiResponse<PasswordVaultEntity> update(@PathVariable Long id, @RequestBody PasswordVaultEntity entity) {
        entity.setId(id);
        Long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(passwordVaultService.update(entity, userId));
    }

    /**
     * 删除密码
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        passwordVaultService.delete(id, userId);
        return ApiResponse.success();
    }

    /**
     * 获取分类列表
     */
    @GetMapping("/categories")
    public ApiResponse<List<String>> categories() {
        Long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(passwordVaultService.getCategories(userId));
    }
}