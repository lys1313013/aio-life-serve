package top.aiolife.record.pojo.query;

import lombok.Data;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025-11-30 14:10
 */
@Data
public class IncomeQuery {
     /**
     * 收入分类id
     */
    private String incTypeId;

    /**
     * 年份
     */
    private String year;
}
