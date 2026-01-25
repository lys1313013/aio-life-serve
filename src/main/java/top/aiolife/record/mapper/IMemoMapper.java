package top.aiolife.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.aiolife.record.pojo.entity.MemoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 备忘录Mapper
 *
 * @author Lys
 * @date 2025/12/07 14:35
 */
@Mapper
public interface IMemoMapper extends BaseMapper<MemoEntity> {
}
