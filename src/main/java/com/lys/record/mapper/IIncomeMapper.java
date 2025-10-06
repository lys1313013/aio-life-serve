package com.lys.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lys.record.pojo.entity.IncomeEntity;
import com.lys.record.pojo.vo.IncStaByYearVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/09/14 21:09
 */
public interface IIncomeMapper extends BaseMapper<IncomeEntity> {

    @Select("""
            SELECT left(inc_date, 4) AS year ,sum(amt) as amt,inc_type_id as type_id
            FROM income where user_id = #{userId} and is_deleted = 0  
            GROUP BY LEFT(inc_date, 4), inc_type_id
            """)
    List<IncStaByYearVO> statisticsByYear(int userId);
    
    @Select("""
            SELECT left(inc_date, 4) AS year, substring(inc_date, 6, 2) AS month ,sum(amt) as amt,inc_type_id as type_id
            FROM income where user_id = #{userId} and is_deleted = 0  
            GROUP BY LEFT(inc_date, 4), substring(inc_date, 6, 2), inc_type_id
            """)
    List<IncStaByYearVO> statisticsByMonth(int userId);
}