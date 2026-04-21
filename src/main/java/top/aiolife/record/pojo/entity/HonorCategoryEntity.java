package top.aiolife.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 荣誉分类实体
 *
 * @author Lys
 * @date 2026/04/11
 */
@Data
@TableName("honor_category")
public class HonorCategoryEntity {

    private Long id;

    private Long userId;

    private String name;

    private String icon;

    private String color;

    private Integer sortOrder;

    private Integer isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime updateTime;
}
