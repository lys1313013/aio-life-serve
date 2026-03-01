package top.aiolife.record.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IncStaByYearVO {

    private Integer year;

    private Integer month;

    private BigDecimal amt;

    private Long typeId;

    private String typeName;
}