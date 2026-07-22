package top.aiolife.record.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeishuChannelConfigVO {
    private boolean configured;
    private boolean enabled;
    private boolean bound;
    private String appId;
    private String receiverOpenId;
    private String receiverName;
    private String openIdMasked;
}
