package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/03 21:02
 */
@Data
@TableName("expense")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 交易金额（支付软件上的支付金额）
     */
    private BigDecimal transactionAmt;
    /**
     * 记账金额
     */
    private BigDecimal amt;
    private Integer expTypeId;
    private Integer payTypeId;
    /**
     * 交易对方
     */
    private String counterparty;

    private String counterpartyAcct;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expTime;
    private Integer userId;

    /**
     * 交易号
     */
    private String transactionId;

    /**
     * 交易描述
     */
    private String expDesc;

    private Integer isDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private Integer createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private Integer updateUser;
}
