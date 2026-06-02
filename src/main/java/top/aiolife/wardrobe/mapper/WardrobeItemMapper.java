package top.aiolife.wardrobe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.wardrobe.pojo.entity.WardrobeItemEntity;

/**
 * 衣柜衣物 Mapper
 */
@Mapper
public interface WardrobeItemMapper extends BaseMapper<WardrobeItemEntity> {
}
