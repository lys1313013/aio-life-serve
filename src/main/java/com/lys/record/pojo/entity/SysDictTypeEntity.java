package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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

    private Integer dictId;
    private String dictName;
    private String dictType;
    private String status;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
}

