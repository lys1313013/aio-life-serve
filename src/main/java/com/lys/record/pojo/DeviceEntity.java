package com.lys.record.pojo;

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

    private String type;

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
