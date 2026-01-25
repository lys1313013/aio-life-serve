package top.aiolife.record.pojo.query;

import lombok.Data;

import java.time.LocalDate;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025-11-02 17:45
 */
@Data
public class TimeWeekQuery {
    private LocalDate startDate;
    private LocalDate endDate;
}