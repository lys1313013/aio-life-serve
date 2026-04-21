package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.aiolife.record.mapper.IAnniversaryRecordMapper;
import top.aiolife.record.pojo.entity.AnniversaryRecordEntity;
import top.aiolife.record.service.IAnniversaryRecordService;

/**
 * 纪念日 Service 实现类
 *
 * @author Lys
 * @date 2026/04/18
 */
@Service
public class AnniversaryRecordServiceImpl extends ServiceImpl<IAnniversaryRecordMapper, AnniversaryRecordEntity> implements IAnniversaryRecordService {
}
