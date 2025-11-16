package com.lys.record.convertor;

import com.lys.record.pojo.entity.ThoughtEntity;
import com.lys.record.pojo.vo.ThoughtVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025-11-16 18:37
 */
@Mapper(builder = @Builder(disableBuilder = true))
public interface ThoughtConvertor {

    ThoughtConvertor INSTANCE = Mappers.getMapper(ThoughtConvertor.class);

    ThoughtVO Entity2VO(ThoughtEntity thoughtEntity);
}
