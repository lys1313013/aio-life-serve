package top.aiolife.record.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationPreferenceVO {
    private String bizType;
    private String description;
    private boolean enabled;
}
