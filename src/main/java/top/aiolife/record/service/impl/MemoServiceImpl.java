package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.aiolife.record.mapper.IMemoMapper;
import top.aiolife.record.pojo.entity.MemoEntity;
import top.aiolife.record.service.IMemoService;
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
