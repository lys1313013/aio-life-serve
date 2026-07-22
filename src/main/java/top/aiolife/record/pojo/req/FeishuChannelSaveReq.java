package top.aiolife.record.pojo.req;

import lombok.Data;

@Data
public class FeishuChannelSaveReq {
    private Boolean enabled;
    private String appId;
    private String appSecret;
    private String openId;
}
