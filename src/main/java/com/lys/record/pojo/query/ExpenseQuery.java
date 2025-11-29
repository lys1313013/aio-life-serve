package com.lys.record.pojo.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025-11-29 21:32
 */
@Data
public class ExpenseQuery {

    /**
     * 分类id
     */
    private String expTypeId;

    /**
     * 备注
     */
    private String remark;

     /**
     * 支出开始时间
     */
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

     /**
     * 支出结束时间
     */
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
