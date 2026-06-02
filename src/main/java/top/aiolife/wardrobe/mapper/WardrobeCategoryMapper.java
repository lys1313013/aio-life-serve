package top.aiolife.wardrobe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.wardrobe.pojo.entity.WardrobeCategoryEntity;

/**
 * 衣柜分类 Mapper
 */
@Mapper
public interface WardrobeCategoryMapper extends BaseMapper<WardrobeCategoryEntity> {
}
