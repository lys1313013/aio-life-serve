package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.aiolife.record.mapper.IHonorRecordMapper;
import top.aiolife.record.pojo.entity.HonorRecordEntity;
import top.aiolife.record.service.IHonorRecordService;

/**
 * 荣誉记录服务实现
 *
 * @author Lys
 * @date 2026/04/11
 */
@Service
public class HonorRecordServiceImpl extends ServiceImpl<IHonorRecordMapper, HonorRecordEntity> implements IHonorRecordService {
}
