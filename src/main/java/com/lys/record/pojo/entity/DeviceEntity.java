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
    private Integer id;

    private Integer userId;

    private String name;

    /**
     * 设备类型
     */
    private String type;

    /**
     * 设备状态
     * 1 使用中
     * 2 已损坏
     * 3 已送人
     * 4 吃灰中
     */
    private String status;

    private String remark;

    private String purchaseDate;

    private BigDecimal purchasePrice;

    private String purchasePlace;

    private String purchaseCompany;

    private String image;

    /**
     * 订单号字段
     */
    private String orderNumber;
}
