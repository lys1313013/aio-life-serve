package com.lys.record.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lys.core.resq.ApiResponse;
import com.lys.record.mapper.ISysDictDataMapper;
import com.lys.record.mapper.ISysDictTypeMapper;
import com.lys.record.pojo.entity.SysDictDataEntity;
import com.lys.record.pojo.entity.SysDictTypeEntity;
import com.lys.record.pojo.vo.SysDictTypeDetailVO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/getByDictType")
    public ApiResponse<SysDictTypeDetailVO> queryById(String dictType) {
//        // 根据类型英文名称查询类型id
        LambdaQueryWrapper<SysDictTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictTypeEntity::getDictType, dictType);
        SysDictTypeEntity sysDictTypeEntity = sysDictTypeMapper.selectOne(queryWrapper);
        if (sysDictTypeEntity == null) {
            return ApiResponse.success();
        }

        LambdaQueryWrapper<SysDictDataEntity> typeQueryWrapper = new LambdaQueryWrapper<>();
        typeQueryWrapper.eq(SysDictDataEntity::getDictId, sysDictTypeEntity.getDictId());
        List<SysDictDataEntity> sysDictDataEntityList = sysDictDataMapper.selectList(typeQueryWrapper);
        SysDictTypeDetailVO sysDictTypeDetailVO = new SysDictTypeDetailVO();
        sysDictTypeDetailVO.setSysDictTypeEntity(sysDictTypeEntity);
        sysDictTypeDetailVO.setDictDetailList(sysDictDataEntityList);

        return ApiResponse.success(sysDictTypeDetailVO);
    }
}
