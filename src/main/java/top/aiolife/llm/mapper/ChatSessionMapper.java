package top.aiolife.llm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.llm.pojo.entity.ConversationEntity;

@Mapper
public interface ChatSessionMapper extends BaseMapper<ConversationEntity> {
}
