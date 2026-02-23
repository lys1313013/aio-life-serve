package top.aiolife.record.convertor;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.aiolife.record.pojo.entity.TimeRecordEntity;
import top.aiolife.record.pojo.req.TimeRecordReq;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2026-02-22 23:06
 */
@Mapper(builder = @Builder(disableBuilder = true))
public interface TimeRecordConvertor {

    TimeRecordConvertor INSTANCE = Mappers.getMapper(TimeRecordConvertor.class);

    TimeRecordEntity Req2Entity(TimeRecordReq timeRecordReq);
}
