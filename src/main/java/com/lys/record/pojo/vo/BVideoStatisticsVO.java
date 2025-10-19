package com.lys.record.pojo.vo;

import lombok.Data;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/19 21:18
 */
@Data
public class BVideoStatisticsVO {

    /**
     * 已学习时长
     */
    private int studiedSeconds;

    /**
     * 未学习时长
     */
    private int unstudiedSeconds = 0;

    /**
     * 总时长
     */
    private int totalSeconds = 0;

    /**
     * 已学习数量
     */
    private int studiedCount = 0;

    /**
     * 待学习数量
     */
    private int unstudiedCount = 0;

    /**
     * 未开始数量
     */
    private int notStartedCount = 0;

    /**
     * 总数量
     */
    private int totalCount = 0;

    /**
     * 学习进度百分比
     */
    private Double progressPercentage = 0.0;
}
