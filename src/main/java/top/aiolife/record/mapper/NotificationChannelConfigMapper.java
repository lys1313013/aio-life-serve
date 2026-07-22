package top.aiolife.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import top.aiolife.record.pojo.entity.NotificationChannelConfigEntity;

@Mapper
public interface NotificationChannelConfigMapper extends BaseMapper<NotificationChannelConfigEntity> {
    @Delete("DELETE FROM notification_channel_config WHERE user_id = #{userId} AND channel = #{channel}")
    int hardDeleteByUserIdAndChannel(@Param("userId") long userId, @Param("channel") String channel);
}
