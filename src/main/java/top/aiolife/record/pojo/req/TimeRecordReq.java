package top.aiolife.record.pojo.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import top.aiolife.record.pojo.entity.ExerciseRecordEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2026-02-22 17:34
 */
@Data
@TableName("time_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeRecordReq {

    /**
     * 分类id
     */
    private String categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * 开始时间 （分钟）包含开始时间
     */
    private Integer startTime;
    /**
     * 结束时间 （分钟） 包含结束时间
     */
    private Integer endTime;
    private String title;
    private String description;
    private Integer duration;

    /**
     * 是否手动添加
     */
    private Long isManual;

    @TableId
    private String id;

    List<ExerciseRecordEntity> exercises;
}
