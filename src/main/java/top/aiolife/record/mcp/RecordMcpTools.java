package top.aiolife.record.mcp;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import top.aiolife.mcp.annotation.McpToolProvider;
import top.aiolife.record.api.ThoughtController;
import top.aiolife.record.api.TimeRecordController;
import top.aiolife.record.api.TimeTrackerCategoryController;
import top.aiolife.record.pojo.entity.entity.TimeTrackerCategoryEntity;
import top.aiolife.record.pojo.req.ThoughtSaveReq;
import top.aiolife.record.mcp.req.TimeRecordDateRangeMcpReq;
import top.aiolife.record.pojo.req.TimeRecordReq;
import top.aiolife.record.mcp.req.TimeRecordSaveMcpReq;
import top.aiolife.record.pojo.vo.TimeRecordDateRangeVO;
import cn.hutool.core.bean.BeanUtil;
import cn.dev33.satoken.stp.StpUtil;
import top.aiolife.record.pojo.entity.TimeRecordEntity;
import top.aiolife.record.service.ITimeRecordService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@McpToolProvider
@RequiredArgsConstructor
public class RecordMcpTools {

    private final TimeRecordController timeRecordController;
    private final ThoughtController thoughtController;
    private final TimeTrackerCategoryController timeTrackerCategoryController;
    private final ITimeRecordService timeRecordService;

    @Tool("查询指定日期范围内的所有时间记录")
    public List<TimeRecordDateRangeVO> time_record_queryByDateRange(TimeRecordDateRangeMcpReq req) {
        return timeRecordController.queryByDateRangeForAI(req).getData();
    }

    @Tool("保存时间记录")
    public String time_record_save(TimeRecordSaveMcpReq req) {
        TimeRecordReq actualReq = new TimeRecordReq();
        BeanUtil.copyProperties(req, actualReq);

        long userId = StpUtil.getLoginIdAsLong();
        LocalDate date = req.getDate() != null ? req.getDate() : LocalDate.now();

        // 开始时间自动从数据库中查询，最后一条 + 1
        TimeRecordEntity lastRecord = timeRecordService.lambdaQuery()
                .eq(TimeRecordEntity::getUserId, userId)
                .eq(TimeRecordEntity::getDate, date)
                .orderByDesc(TimeRecordEntity::getEndTime)
                .last("limit 1")
                .one();

        int startTime = 0;
        if (lastRecord != null && lastRecord.getEndTime() != null) {
            startTime = lastRecord.getEndTime() + 1;
        }

        // 结束时间为当前时间
        LocalTime now = LocalTime.now();
        int endTime = now.getHour() * 60 + now.getMinute();

        if (endTime < startTime) {
            endTime = startTime;
        }

        // 限制最大值为 1439
        if (startTime > 1439) startTime = 1439;
        if (endTime > 1439) endTime = 1439;

        actualReq.setStartTime(startTime);
        actualReq.setEndTime(endTime);
        actualReq.setDuration(endTime - startTime + 1);
        if (actualReq.getDate() == null) {
            actualReq.setDate(date);
        }

        timeRecordController.save(actualReq);

        // 格式化时间并返回给大模型
        String startTimeStr = String.format("%02d:%02d", startTime / 60, startTime % 60);
        String endTimeStr = String.format("%02d:%02d", endTime / 60, endTime % 60);
        return String.format("保存成功！开始时间：%s，结束时间：%s，持续时长：%d分钟", startTimeStr, endTimeStr, actualReq.getDuration());
    }

    @Tool("保存一条想法，并可附带多个关联事件")
    public boolean thought_save(ThoughtSaveReq req) {
        thoughtController.save(req);
        return true;
    }

    @Tool("查询用户的所有时迹分类（含合并的公共分类）")
    public List<TimeTrackerCategoryEntity> time_tracker_category_list() {
        return timeTrackerCategoryController.list().getData();
    }
}
