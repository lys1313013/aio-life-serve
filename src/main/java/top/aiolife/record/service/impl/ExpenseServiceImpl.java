package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.aiolife.record.mapper.IExpenseMapper;
import top.aiolife.record.pojo.entity.ExpenseEntity;
import top.aiolife.record.service.IExpenseService;
import org.springframework.stereotype.Service;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/05 21:42
 */
@Service
public class ExpenseServiceImpl extends ServiceImpl<IExpenseMapper, ExpenseEntity> implements IExpenseService {
}
