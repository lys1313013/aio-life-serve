package top.aiolife.record.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeishuRecipientVO {
    private String openId;
    private String name;
}
