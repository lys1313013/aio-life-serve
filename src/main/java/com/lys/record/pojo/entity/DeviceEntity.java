package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 电子产品
 *
 * @author Lys
 * @date 2025/04/04 19:14
 */
@Getter
@Setter
@TableName("device")
public class DeviceEntity {
    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 设备的名称
     */
    private String name;

    /**
     * 设备类型
     */
    private String type;

    /**
     * 设备状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 设备的购买日期
     */
    private String purchaseDate;

    /**
     * 设备的购买价格
     */
    private BigDecimal purchasePrice;

    /**
     * 设备的购买地点
     */
    private String purchasePlace;

    /**
     * 设备的图片链接或存储路径
     */
    private String image;

    /**
     * 订单号字段
     */
    private String orderNumber;
}
