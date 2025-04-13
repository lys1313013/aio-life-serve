package com.lys.record.pojo.vo;

import lombok.Data;

/**
 * 看板卡片VO
 *
 * @author Lys
 * @date 2025/04/13 14:26
 */
@Data
public class DashboardCardVO {
    /**
     * 卡片图标 (待优化）
     * packages\icons\src\svg
     */
    private String icon = "svg:card";
    /**
     * 卡片标题
     */
    private String title;

    /**
     * 当前值
     */
    private Integer value;

    /**
     * 总量标题
     */
    private String totalTitle;
    /**
     * 总量值
     */
    private Integer totalValue;
}
