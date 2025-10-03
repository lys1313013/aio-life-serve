package com.lys.record.convertor;

import com.lys.record.pojo.entity.SysDictDataEntity;
import com.lys.record.pojo.vo.SysDictDataVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;

/**
 * 数据字典数据转换
 *
 * @author Lys
 * @date 2025/04/07 23:09
 */
@Mapper(builder = @Builder(disableBuilder = true))
public interface SysDictDataConvertor {

    SysDictDataConvertor INSTANCE = Mappers.getMapper(SysDictDataConvertor.class);

    @Mapping(source = "dictLabel", target = "label")
    @Mapping(source = "dictValue", target = "value")
    @Mapping(source = "dictCode", target = "id")
    SysDictDataVO Entity2VO(SysDictDataEntity sysDictDataEntity);
}
