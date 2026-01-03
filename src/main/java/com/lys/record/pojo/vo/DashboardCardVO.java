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
     * 卡片图标
     * <a href="https://icon-sets.iconify.design/"></a> 这个网站找
     */
    private String icon = "svg:card";

    /**
     * 点击图标跳转链接
     */
    private String iconClickUrl;

    /**
     * 卡片标题
     */
    private String title;

    /**
     * 点击标题跳转链接
     */
    private String titleClickUrl;

    /**
     * 当前值
     */
    private String value;

    /**
     * 值颜色
     */
    private String valueColor;

    /**
     * 总量标题
     */
    private String totalTitle;
    /**
     * 总量值
     */
    private String totalValue;
}
