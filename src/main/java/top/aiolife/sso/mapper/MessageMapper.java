package top.aiolife.sso.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.sso.pojo.entity.MessageEntity;

@Mapper
public interface MessageMapper extends BaseMapper<MessageEntity> {
}
