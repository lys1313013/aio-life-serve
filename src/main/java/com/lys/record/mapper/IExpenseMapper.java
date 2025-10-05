package com.lys.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lys.record.pojo.entity.ExpenseEntity;
import com.lys.record.pojo.vo.ExpStaByYearVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/03 21:50
 */
public interface IExpenseMapper extends BaseMapper<ExpenseEntity> {

    @Select("""
            SELECT left(exp_time, 4) AS year ,sum(amt) as amt,exp_type_id AS type_id
            FROM expense
            WHERE user_id = #{userId} and is_deleted = 0  
            GROUP BY LEFT(exp_time, 4), exp_type_id
            """)
    List<ExpStaByYearVO> statisticsByYear(int userId);
}
