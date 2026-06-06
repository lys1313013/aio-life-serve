package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.core.util.SysUtil;
import top.aiolife.record.mapper.UserDictDataMapper;
import top.aiolife.record.mapper.UserDictTypeMapper;
import top.aiolife.record.pojo.entity.entity.UserDictDataEntity;
import top.aiolife.record.pojo.entity.entity.UserDictTypeEntity;
import top.aiolife.record.pojo.vo.UserDictTypeDetailVO;
import top.aiolife.record.service.UserDictDataService;

import top.aiolife.record.enums.DictTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 用户字典类型Controller
 *
 * @author Lys
 */
@RestController
@AllArgsConstructor
@RequestMapping("/userDictType")
public class UserDictTypeController {

    private UserDictTypeMapper userDictTypeMapper;
    private UserDictDataService userDictDataService;

    @GetMapping("/dictTypeEnum")
    public ApiResponse<List<Map<String, String>>> dictTypeEnum() {
        return ApiResponse.success(DictTypeEnum.toList());
    }

    @GetMapping("/getByDictType")
    public ApiResponse<UserDictTypeDetailVO> getByDictType(String dictType) {
        Long userId = StpUtil.getLoginIdAsLong();

        LambdaQueryWrapper<UserDictTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDictTypeEntity::getDictType, dictType);
        queryWrapper.eq(UserDictTypeEntity::getUserId, userId);

        UserDictTypeEntity typeEntity = userDictTypeMapper.selectOne(queryWrapper);
        if (typeEntity == null) {
            // 可以选择创建一个虚拟的公共分类实体返回，或者仅返回数据
            typeEntity = new UserDictTypeEntity();
            typeEntity.setDictType(dictType);
        }

        // 调用 Service 获取合并后的用户可见字典数据
        List<UserDictDataEntity> dataList = userDictDataService.listUserVisibleDictData(userId, dictType);

        UserDictTypeDetailVO detailVO = new UserDictTypeDetailVO();
        detailVO.setUserDictTypeEntity(typeEntity);
        detailVO.setDictDetailList(dataList);

        return ApiResponse.success(detailVO);
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<UserDictTypeEntity>> query(@RequestBody CommonQuery<UserDictTypeEntity> query) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<UserDictTypeEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserDictTypeEntity::getUserId, userId);

        UserDictTypeEntity condition = query.getCondition();
        if (condition != null) {
            lambdaQueryWrapper.like(SysUtil.isNotEmpty(condition.getDictName()),
                    UserDictTypeEntity::getDictName, condition.getDictName());
            lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getDictType()),
                    UserDictTypeEntity::getDictType, condition.getDictType());
            lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getStatus()),
                    UserDictTypeEntity::getStatus, condition.getStatus());
        }

        Page<UserDictTypeEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<UserDictTypeEntity> iPage = userDictTypeMapper.selectPage(page, lambdaQueryWrapper);
        PageResp<UserDictTypeEntity> pageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(pageResp);
    }

    @PostMapping
    public ApiResponse<Boolean> insert(@RequestBody UserDictTypeEntity entity) {
        Long userId = StpUtil.getLoginIdAsLong();
        entity.setUserId(userId);
        entity.fillCreateCommonField(userId);
        boolean b = userDictTypeMapper.insert(entity) > 0;
        return ApiResponse.success(b);
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody UserDictTypeEntity entity) {
        Long userId = StpUtil.getLoginIdAsLong();
        entity.fillUpdateCommonField(userId);

        LambdaQueryWrapper<UserDictTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDictTypeEntity::getId, entity.getId());
        queryWrapper.eq(UserDictTypeEntity::getUserId, userId);

        boolean b = userDictTypeMapper.update(entity, queryWrapper) > 0;
        return ApiResponse.success(b);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id) {
        Long userId = StpUtil.getLoginIdAsLong();

        LambdaQueryWrapper<UserDictTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDictTypeEntity::getId, id);
        queryWrapper.eq(UserDictTypeEntity::getUserId, userId);

        boolean b = userDictTypeMapper.delete(queryWrapper) > 0;
        return ApiResponse.success(b);
    }

    // ================= 管理员 API =================

    @cn.dev33.satoken.annotation.SaCheckRole("admin")
    @PostMapping("/admin/query")
    public ApiResponse<PageResp<UserDictTypeEntity>> adminQuery(@RequestBody CommonQuery<UserDictTypeEntity> query) {
        LambdaQueryWrapper<UserDictTypeEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        UserDictTypeEntity condition = query.getCondition();
        if (condition != null) {
            lambdaQueryWrapper.eq(condition.getUserId() != null,
                    UserDictTypeEntity::getUserId, condition.getUserId());
            lambdaQueryWrapper.like(SysUtil.isNotEmpty(condition.getDictName()),
                    UserDictTypeEntity::getDictName, condition.getDictName());
            lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getDictType()),
                    UserDictTypeEntity::getDictType, condition.getDictType());
            lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getStatus()),
                    UserDictTypeEntity::getStatus, condition.getStatus());
        }

        Page<UserDictTypeEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<UserDictTypeEntity> iPage = userDictTypeMapper.selectPage(page, lambdaQueryWrapper);
        PageResp<UserDictTypeEntity> pageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(pageResp);
    }

    @cn.dev33.satoken.annotation.SaCheckRole("admin")
    @DeleteMapping("/admin/{id}")
    public ApiResponse<Boolean> adminDelete(@PathVariable("id") Long id) {
        boolean b = userDictTypeMapper.deleteById(id) > 0;
        return ApiResponse.success(b);
    }
}
