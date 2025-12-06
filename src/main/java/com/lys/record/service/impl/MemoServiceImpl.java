package com.lys.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lys.record.mapper.IMemoMapper;
import com.lys.record.pojo.entity.MemoEntity;
import com.lys.record.service.IMemoService;
import org.springframework.stereotype.Service;

/**
 * 备忘录Service实现
 *
 * @author Lys
 * @date 2025/12/07 14:35
 */
@Service
public class MemoServiceImpl extends ServiceImpl<IMemoMapper, MemoEntity> implements IMemoService {
}
