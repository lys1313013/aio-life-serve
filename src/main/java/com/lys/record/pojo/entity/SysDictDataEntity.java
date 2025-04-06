package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 字典数据实体
 *
 * @author Lys
 * @date 2025/04/06 00:33
 */
@Getter
@Setter
@TableName("sys_dict_data")
public class SysDictDataEntity {

    @TableId
    private Integer dictCode;
    private Integer dictId;

    @TableField(exist = false)
    private String dictName;
    @TableField(exist = false)
    private String dictType;

    private Integer dictSort;
    private String dictLabel;
    private String dictValue;
    private String cssClass;
    private String listClass;
    private String isDefault;
    private String status;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private String remark;
}

