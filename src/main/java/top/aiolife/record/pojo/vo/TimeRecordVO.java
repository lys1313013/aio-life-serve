package top.aiolife.record.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.aiolife.record.pojo.entity.ExerciseRecordEntity;
import top.aiolife.record.pojo.entity.TimeRecordEntity;

import java.util.List;

/**
 * TimeRecord VO
 *
 * @author Lys
 * @date 2026-02-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TimeRecordVO extends TimeRecordEntity {
    /**
     * 运动记录列表
     */
    private List<ExerciseRecordEntity> exercises;
}
