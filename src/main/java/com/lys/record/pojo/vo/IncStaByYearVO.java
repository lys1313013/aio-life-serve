package com.lys.record.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IncStaByYearVO {

    private Integer year;

    private BigDecimal incAmt;

    private Integer incTypeId;

    private String incTypeName;
}