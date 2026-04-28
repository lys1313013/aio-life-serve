package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.record.mapper.IPasswordVaultMapper;
import top.aiolife.record.pojo.entity.PasswordVaultEntity;
import top.aiolife.record.service.IPasswordVaultService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 密码库控制器
 *
 * @author Lys
 * @date 2026/04/28
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/password")
public class PasswordVaultController {

    private final IPasswordVaultService passwordVaultService;
    private final IPasswordVaultMapper passwordVaultMapper;

    /**
     * 查询密码列表
     */
    @GetMapping("/list")
    public ApiResponse<List<PasswordVaultEntity>> list() {
        long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<PasswordVaultEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PasswordVaultEntity::getUserId, userId);
        wrapper.orderByDesc(PasswordVaultEntity::getUpdateTime);
        List<PasswordVaultEntity> list = passwordVaultMapper.selectList(wrapper);
        return ApiResponse.success(list);
    }

    /**
     * 获取单条密码详情
     */
    @GetMapping("/{id}")
    public ApiResponse<PasswordVaultEntity> getById(@PathVariable Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<PasswordVaultEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PasswordVaultEntity::getId, id);
        wrapper.eq(PasswordVaultEntity::getUserId, userId);
        PasswordVaultEntity entity = passwordVaultMapper.selectOne(wrapper);
        return ApiResponse.success(entity);
    }

    /**
     * 新增密码
     */
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody PasswordVaultEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setCreateUser(StpUtil.getLoginIdAsLong());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        if (entity.getFavorite() == null) {
            entity.setFavorite(false);
        }
        if (entity.getCategory() == null || entity.getCategory().isEmpty()) {
            entity.setCategory("其他");
        }
        passwordVaultMapper.insert(entity);
        return ApiResponse.success(true);
    }

    /**
     * 编辑密码
     */
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody PasswordVaultEntity entity) {
        Long userId = StpUtil.getLoginIdAsLong();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setUpdateUser(userId);
        entity.setUpdateTime(LocalDateTime.now());
        passwordVaultMapper.updateById(entity);
        return ApiResponse.success(true);
    }

    /**
     * 删除密码
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<PasswordVaultEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PasswordVaultEntity::getId, id);
        wrapper.eq(PasswordVaultEntity::getUserId, userId);
        passwordVaultMapper.delete(wrapper);
        return ApiResponse.success(true);
    }

    /**
     * 获取分类列表
     */
    @GetMapping("/categories")
    public ApiResponse<List<String>> categories() {
        List<String> categories = Arrays.asList("工作", "生活", "金融", "社交", "其他");
        return ApiResponse.success(categories);
    }
}