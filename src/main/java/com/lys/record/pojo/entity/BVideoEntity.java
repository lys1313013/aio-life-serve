package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/06 22:48
 */
@Data
@TableName("bilibili_video")
public class BVideoEntity extends BaseEntity{

    /**
     * 视频标题
     */
    private String title;

    /**
     * B站视频URL
     */
    private String url;

    /**
     * 视频封面URL
     */
    private String cover;

    /**
     * 视频时长（格式：HH:MM:SS）
     */
    private String duration;

    /**
     * 总集数
     */
    private Integer episodes = 1;

    /**
     * 当前观看集数
     */
    private Integer currentEpisode = 1;

    /**
     * 观看进度（百分比）
     */
    private Double progress = 0.0;

    /**
     * 学习状态
     */
    private Integer status;

    /**
     * 学习笔记
     */
    private String notes;

    /**
     * BV号
     */
    private String bvid;

    /**
     * AV号
     */
    private String aid;

    /**
     * 视频描述
     */
    private String description;

    /**
     * UP主信息（JSON格式存储）
     */
    private String ownerName;

    /**
     * 视频统计信息（JSON格式存储）
     */
    private String statInfo;

    /**
     * 发布时间
     */
    private LocalDateTime pubdate;

    private Integer userId;
    private Integer createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private Integer updateUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 版权信息
     */
    private Integer copyright = 1;

    /**
     * 分集信息（JSON格式存储）
     */
    private String pagesInfo;
}