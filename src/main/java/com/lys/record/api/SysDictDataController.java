package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lys.core.query.CommonQuery;
import com.lys.core.resq.ApiResponse;
import com.lys.core.resq.PageResp;
import com.lys.core.util.SysUtil;
import com.lys.record.mapper.ISysDictDataMapper;
import com.lys.record.mapper.ISysDictTypeMapper;
import com.lys.record.pojo.entity.SysDictDataEntity;
import com.lys.record.pojo.entity.SysDictTypeEntity;
import com.lys.record.pojo.query.SysDictTypeQuery;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

/**
 * 字典数据Controller
 *
 * @author Lys
 * @date 2025/04/06 17:24
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sysDictData")
public class SysDictDataController {
    private ISysDictDataMapper sysDictDataMapper;
    private ISysDictTypeMapper sysDictTypeMapper;

    public ISysDictDataMapper getBaseMapper() {
        return sysDictDataMapper;
    }


    @PostMapping("/query")
    public ApiResponse<PageResp<SysDictDataEntity>> query(
            @RequestBody CommonQuery<SysDictTypeQuery> query) {
        LambdaQueryWrapper<SysDictDataEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(SysDictDataEntity::getDictId, SysDictDataEntity::getDictSort);

        SysDictTypeQuery condition = query.getCondition();

        // 字典名称使用字典类型表过滤查询
        if (SysUtil.isNotEmpty(condition.getDictName())) {
            LambdaQueryWrapper<SysDictTypeEntity> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.like(SysDictTypeEntity::getDictName, condition.getDictName());
            List<SysDictTypeEntity> sysDictTypeEntities = sysDictTypeMapper.selectList(lambdaQueryWrapper1);
            if (SysUtil.isEmpty(sysDictTypeEntities)) {
                return ApiResponse.success();
            } else {
                Collection<Integer> dictIdList = sysDictTypeEntities.stream().map(SysDictTypeEntity::getDictId).distinct().toList();
                lambdaQueryWrapper.in(SysDictDataEntity::getDictId, dictIdList);
            }
        }

        // 分页
        Page<SysDictDataEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<SysDictDataEntity> iPage = getBaseMapper().selectPage(page, lambdaQueryWrapper);

        List<SysDictDataEntity> records = iPage.getRecords();
        // 补充dictName
        Collection<Integer> dictIdList = records.stream().map(SysDictDataEntity::getDictId).distinct().toList();
        List<SysDictTypeEntity> sysDictTypeEntities = sysDictTypeMapper.selectByIds(dictIdList);
        records.forEach(sysDictDataEntity -> {
            for (SysDictTypeEntity sysDictTypeEntity : sysDictTypeEntities)
                if (sysDictDataEntity.getDictId().equals(sysDictTypeEntity.getDictId())) {
                    sysDictDataEntity.setDictName(sysDictTypeEntity.getDictName());
                    sysDictDataEntity.setDictType(sysDictTypeEntity.getDictType());
                    break;
                }
        });
        PageResp<SysDictDataEntity> objectPageResp = PageResp.of(records, iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }


    @PostMapping("/insertOrUpdate")
    public ApiResponse<Boolean> insertOrUpdate(@RequestBody SysDictDataEntity entity) {
        entity.setCreateBy(StpUtil.getLoginIdAsString());
        entity.setUpdateBy(StpUtil.getLoginIdAsString());
        boolean b = getBaseMapper().insertOrUpdate(entity);
        return ApiResponse.success(b);
    }

    @PostMapping("/delete")
    public ApiResponse<Boolean> delete(@RequestBody SysDictDataEntity entity) {
        boolean b = getBaseMapper().deleteById(entity) > 0;
        return ApiResponse.success(b);
    }
}
