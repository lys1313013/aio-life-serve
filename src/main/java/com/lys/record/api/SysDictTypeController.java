package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lys.record.convertor.SysDictDataConvertor;
import com.lys.core.query.CommonQuery;
import com.lys.core.resq.ApiResponse;
import com.lys.core.resq.PageResp;
import com.lys.core.util.SysUtil;
import com.lys.record.mapper.ISysDictDataMapper;
import com.lys.record.mapper.ISysDictTypeMapper;
import com.lys.record.pojo.entity.SysDictDataEntity;
import com.lys.record.pojo.entity.SysDictTypeEntity;
import com.lys.record.pojo.vo.SysDictDataVO;
import com.lys.record.pojo.vo.SysDictTypeDetailVO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典Controller
 *
 * @author Lys
 * @date 2025/04/05 22:17
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sysDictType")
public class SysDictTypeController {

    private ISysDictTypeMapper sysDictTypeMapper;
    private ISysDictDataMapper sysDictDataMapper;

    public ISysDictTypeMapper getBaseMapper() {
        return sysDictTypeMapper;
    }

    @GetMapping("/getByDictType")
    public ApiResponse<SysDictTypeDetailVO> queryById(String dictType) {
        // 根据类型英文名称查询类型id
        LambdaQueryWrapper<SysDictTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictTypeEntity::getDictType, dictType);
        SysDictTypeEntity sysDictTypeEntity = sysDictTypeMapper.selectOne(queryWrapper);
        if (sysDictTypeEntity == null) {
            return ApiResponse.success();
        }

        LambdaQueryWrapper<SysDictDataEntity> typeQueryWrapper = new LambdaQueryWrapper<>();
        typeQueryWrapper.eq(SysDictDataEntity::getDictId, sysDictTypeEntity.getDictId());
        typeQueryWrapper.orderByAsc(SysDictDataEntity::getDictSort);
        List<SysDictDataEntity> sysDictDataEntityList = sysDictDataMapper.selectList(typeQueryWrapper);
        SysDictTypeDetailVO sysDictTypeDetailVO = new SysDictTypeDetailVO();
        sysDictTypeDetailVO.setSysDictTypeEntity(sysDictTypeEntity);

        List<SysDictDataVO> detailVoList = sysDictDataEntityList.stream().map(SysDictDataConvertor.INSTANCE::Entity2VO).toList();
        sysDictTypeDetailVO.setDictDetailList(detailVoList);

        return ApiResponse.success(sysDictTypeDetailVO);
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<SysDictTypeEntity>> query(
            @RequestBody CommonQuery<SysDictTypeEntity> query) {
        LambdaQueryWrapper<SysDictTypeEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        SysDictTypeEntity condition = query.getCondition();
        if (condition != null) {
            lambdaQueryWrapper.like(SysUtil.isNotEmpty(condition.getDictName()),
                    SysDictTypeEntity::getDictName , condition.getDictName());
        }

        // 分页
        Page<SysDictTypeEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<SysDictTypeEntity> iPage = getBaseMapper().selectPage(page, lambdaQueryWrapper);
        PageResp<SysDictTypeEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }


    @PostMapping("/insertOrUpdate")
    public ApiResponse<Boolean> insertOrUpdate(@RequestBody SysDictTypeEntity entity) {
        entity.setCreateBy(StpUtil.getLoginIdAsString());
        entity.setUpdateBy(StpUtil.getLoginIdAsString());
        boolean b = getBaseMapper().insertOrUpdate(entity);
        return ApiResponse.success(b);
    }

    @PostMapping("/delete")
    public ApiResponse<Boolean> delete(@RequestBody SysDictTypeEntity entity) {
        entity.setUpdateBy(StpUtil.getLoginIdAsString());
        boolean b = getBaseMapper().deleteById(entity) > 0;
        return ApiResponse.success(b);
    }
}
