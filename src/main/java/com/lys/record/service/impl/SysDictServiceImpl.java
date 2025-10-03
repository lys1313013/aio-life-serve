package com.lys.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lys.record.mapper.ISysDictDataMapper;
import com.lys.record.mapper.ISysDictTypeMapper;
import com.lys.record.pojo.entity.SysDictDataEntity;
import com.lys.record.pojo.entity.SysDictTypeEntity;
import com.lys.record.service.ISysDictService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/03 14:25
 */
@Service
@AllArgsConstructor
public class SysDictServiceImpl implements ISysDictService {

    private ISysDictDataMapper sysDictDataMapper;

    private ISysDictTypeMapper sysDictTypeMapper;

    @Override
    public List<SysDictDataEntity> getDictDataByDictType(String dictType) {
        LambdaQueryWrapper<SysDictTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictTypeEntity::getDictType, dictType);
        SysDictTypeEntity sysDictTypeEntity = sysDictTypeMapper.selectOne(queryWrapper);
        if (sysDictTypeEntity == null) {
            return List.of();
        }
        LambdaQueryWrapper<SysDictDataEntity> typeQueryWrapper = new LambdaQueryWrapper<>();
        typeQueryWrapper.eq(SysDictDataEntity::getDictId, sysDictTypeEntity.getDictId());
        typeQueryWrapper.orderByAsc(SysDictDataEntity::getDictSort);
        return sysDictDataMapper.selectList(typeQueryWrapper);
    }
}
