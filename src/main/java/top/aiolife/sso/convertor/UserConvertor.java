package top.aiolife.sso.convertor;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.aiolife.sso.pojo.entity.UserEntity;
import top.aiolife.sso.pojo.vo.UserVO;

/**
 * 用户对象转换器
 *
 * @author Lys
 * @date 2026/03/09
 */
@Mapper(builder = @Builder(disableBuilder = true))
public interface UserConvertor {

    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);

    UserVO entity2VO(UserEntity userEntity);
}
