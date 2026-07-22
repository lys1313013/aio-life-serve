package top.aiolife.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notification_preference")
public class NotificationPreferenceEntity extends BaseEntity {
    private Long userId;
    private String bizType;
    private String channel;
    private Integer enabled;
}
