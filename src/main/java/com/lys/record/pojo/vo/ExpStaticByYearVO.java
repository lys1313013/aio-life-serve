package com.lys.record.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/06 00:28
 */
@Data
public class ExpStaticByYearVO {
    private Integer year;
    private Integer month;
    private List<ExpStaByYearVO> detail;
}
