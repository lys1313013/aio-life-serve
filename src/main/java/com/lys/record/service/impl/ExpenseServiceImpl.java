package com.lys.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lys.record.mapper.IExpenseMapper;
import com.lys.record.pojo.entity.ExpenseEntity;
import com.lys.record.service.IExpenseService;
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
