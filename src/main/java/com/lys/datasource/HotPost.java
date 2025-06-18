package com.lys.datasource;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 热点表
 *
 * @author cong
 */
@TableName(value = "hot_post")
@Data
@Builder
public class HotPost {
    /**
     * id
     */
    private Long id;

    /**
     * 排行榜名称
     */
    private String name;

    /**
     * 热点类型名称
     */
    private String typeName;

    /**
     * 热点类型
     */
    private String type;

    /**
     * 图标地址
     */
    private String iconUrl;

    /**
     * 热点数据（json）
     */
    private String hostJson;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 分类
     */
    private Integer category;

    /**
     * 更新间隔，以小时为单位
     */
    private BigDecimal updateInterval;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;
}