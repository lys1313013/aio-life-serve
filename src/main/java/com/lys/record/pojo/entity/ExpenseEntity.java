package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/03 21:02
 */
@Data
@TableName("expense")
public class ExpenseEntity {
    @TableId
    private Integer id;
    private BigDecimal amt;
    private Integer expTypeId;
    private Integer payTypeId;
    private String remark;
    private String expDate;
    private Integer userId;
    private Integer isDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
