package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 演出记录
 *
 * @author Lys
 * @date 2025/04/04
 */
@Getter
@Setter
@TableName("performance")
public class PerformanceEntity {
    /**
     * 唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 演出名称
     */
    private String performanceName;

    /**
     * 主要演员/演出团体
     */
    private String performer;

    /**
     * 演出类型(演唱会/话剧/音乐会等)
     */
    private String performanceType;

    /**
     * 演出日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate performanceDate;

    /**
     * 演出城市
     */
    private String city;

    /**
     * 演出地点
     */
    private String venue;

    /**
     * 票价
     */
    private BigDecimal ticketPrice;

    /**
     * 座位信息
     */
    private String seatInfo;

    /**
     * 演出时长(分钟)
     */
    private Integer duration;

    /**
     * 演出评分(1-5)
     */
    private Integer rating;

    /**
     * 演出评价
     */
    private String review;

    /**
     * 演出海报/票根图片链接
     */
    private String imageUrl;

    /**
     * 购票平台
     */
    private String purchasePlatform;

    /**
     * 购票订单号
     */
    private String orderNumber;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private Integer updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}