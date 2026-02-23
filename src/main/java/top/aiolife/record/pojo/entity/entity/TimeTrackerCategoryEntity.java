package top.aiolife.record.pojo.entity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.aiolife.record.pojo.entity.BaseEntity;

/**
 * 时间追踪-分类配置表(TimeTrackerCategory)表实体类
 *
 * @author Lys1313013
 * @since 2026-02-16 18:48:22
 */
@Data
@TableName("time_tracker_category")
public class TimeTrackerCategoryEntity extends BaseEntity {
    //用户ID
    private Integer userId;
    //分类标识(如: rest, work)
    private String code;
    //分类名称
    private String name;
    //颜色值(Hex)
    private String color;
    //描述
    private String description;
    //是否记录时间
    private Integer isTrackTime;
    //排序权重
    private Integer sort;
    //是否删除
    private Integer isDeleted;
}

