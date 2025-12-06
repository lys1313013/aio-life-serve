package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 备忘录实体
 *
 * @author Lys
 * @date 2025/12/07 14:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("memo")
public class MemoEntity extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容
     */
    private String content;
}
