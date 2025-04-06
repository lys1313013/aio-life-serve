package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 字典类型实体
 *
 * @author Lys
 * @date 2025/04/06 00:34
 */
@Getter
@Setter
@TableName("sys_dict_type")
public class SysDictTypeEntity {

    @TableId
    private Integer dictId;
    private String dictName;
    private String dictType;
    private String status;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private String remark;
}

