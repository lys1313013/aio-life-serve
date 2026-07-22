package top.aiolife.record.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FeishuRecipientListVO {
    private List<FeishuRecipientVO> items;
    private String warning;
}
