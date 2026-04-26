package top.aiolife.record.pojo.req;

import lombok.Data;

import java.util.List;

/**
 * 想法保存请求体
 *
 * @author GPT
 * @date 2026/04/25
 */
@Data
public class ThoughtSaveReq {

    private String content;

    private List<ThoughtSaveEventReq> events;
}
