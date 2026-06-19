package top.aiolife.record.convertor;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.aiolife.record.pojo.entity.MovieEntity;
import top.aiolife.record.pojo.req.MovieReq;
import top.aiolife.record.pojo.vo.MovieVO;

import java.util.List;

@Mapper
public interface MovieConvertor {

    MovieConvertor INSTANCE = Mappers.getMapper(MovieConvertor.class);

    MovieEntity reqToEntity(MovieReq req);

    MovieVO entityToVO(MovieEntity entity);

    List<MovieVO> entityToVOList(List<MovieEntity> list);
}