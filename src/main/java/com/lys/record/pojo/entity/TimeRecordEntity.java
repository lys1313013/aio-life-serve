package com.lys.record.pojo.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("time_record")
public class TimeRecordEntity {

    private Long userId;
    private String categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * 开始时间
     */
    private Long startTime;
    private Long endTime;
    private String title;
    private String description;
    private Long duration;
    private Long isManual;

    @TableId
    private String id;

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Integer isDeleted;
}
