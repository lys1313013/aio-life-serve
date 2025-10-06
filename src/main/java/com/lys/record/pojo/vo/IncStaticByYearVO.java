package com.lys.record.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/03 01:11
 */
@Data
public class IncStaticByYearVO {
    private Integer year;

    private Integer month;

    private List<IncStaByYearVO> detail;

}