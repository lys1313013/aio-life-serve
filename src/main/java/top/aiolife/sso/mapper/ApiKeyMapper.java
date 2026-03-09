package top.aiolife.sso.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.sso.pojo.entity.ApiKeyEntity;

/**
 * API Key Mapper
 *
 * @author Lys
 * @date 2026/03/09
 */
@Mapper
public interface ApiKeyMapper extends BaseMapper<ApiKeyEntity> {
}
