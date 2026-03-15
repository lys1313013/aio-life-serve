package top.aiolife.llm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.llm.pojo.entity.LLMKeyEntity;

import java.util.List;

@Mapper
public interface LLMKeyMapper extends BaseMapper<LLMKeyEntity> {

    /**
     * 根据用户ID获取大模型配置列表
     * @param userId 用户ID
     * @return 大模型配置列表
     */
    List<LLMKeyEntity> selectByUserId(Long userId);

    /**
     * 根据用户ID获取默认大模型配置
     * @param userId 用户ID
     * @return 默认大模型配置
     */
    LLMKeyEntity selectDefaultByUserId(Long userId);
}
