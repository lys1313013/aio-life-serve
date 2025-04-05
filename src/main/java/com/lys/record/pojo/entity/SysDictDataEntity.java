package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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

    private Integer dictCode;
    private Integer dictId;
    private Integer dictSort;
    private String dictLabel;
    private String dictValue;
    private String dictType;
    private String cssClass;
    private String listClass;
    private String isDefault;
    private String status;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
}

