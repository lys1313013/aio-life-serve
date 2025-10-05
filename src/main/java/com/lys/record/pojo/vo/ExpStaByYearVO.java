package com.lys.record.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/06 00:25
 */
@Data
public class ExpStaByYearVO {

    private Integer year;

    private BigDecimal amt;

    private Integer typeId;

    private String typeName;
}
