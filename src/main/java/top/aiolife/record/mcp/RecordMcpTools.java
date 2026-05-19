package top.aiolife.record.mcp;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import top.aiolife.mcp.annotation.McpToolProvider;
import top.aiolife.record.api.ThoughtController;
import top.aiolife.record.api.TimeRecordController;
import top.aiolife.record.pojo.req.ThoughtSaveReq;
import top.aiolife.record.pojo.req.TimeRecordDateRangeReq;
import top.aiolife.record.pojo.req.TimeRecordReq;
import top.aiolife.record.mcp.req.TimeRecordSaveMcpReq;
import top.aiolife.record.pojo.vo.TimeRecordDateRangeVO;
import cn.hutool.core.bean.BeanUtil;

import java.util.List;

@McpToolProvider
@RequiredArgsConstructor
public class RecordMcpTools {

    private final TimeRecordController timeRecordController;
    private final ThoughtController thoughtController;

    @Tool("查询指定日期范围内的所有时间记录")
    public List<TimeRecordDateRangeVO> time_record_queryByDateRange(TimeRecordDateRangeReq req) {
        return timeRecordController.queryByDateRangeForAI(req).getData();
    }

    @Tool("保存时间记录")
    public boolean time_record_save(TimeRecordSaveMcpReq req) {
        TimeRecordReq actualReq = new TimeRecordReq();
        BeanUtil.copyProperties(req, actualReq);
        timeRecordController.save(actualReq);
        return true;
    }

    @Tool("保存一条想法，并可附带多个关联事件")
    public boolean thought_save(ThoughtSaveReq req) {
        thoughtController.save(req);
        return true;
    }
}
