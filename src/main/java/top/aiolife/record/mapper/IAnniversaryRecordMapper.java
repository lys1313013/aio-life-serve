package top.aiolife.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.record.pojo.entity.AnniversaryRecordEntity;

/**
 * 纪念日 Mapper
 *
 * @author Lys
 * @date 2026/04/18
 */
@Mapper
public interface IAnniversaryRecordMapper extends BaseMapper<AnniversaryRecordEntity> {
}
