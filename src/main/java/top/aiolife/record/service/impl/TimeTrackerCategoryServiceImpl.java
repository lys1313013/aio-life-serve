package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.aiolife.record.mapper.ITimeTrackerCategoryMapper;
import top.aiolife.record.pojo.entity.entity.TimeTrackerCategoryEntity;
import top.aiolife.record.service.ITimeTrackerCategoryService;

/**
 * 时间追踪-分类配置表(TimeTrackerCategory) Service 实现类
 *
 * @author Lys1313013
 * @since 2026-03-07
 */
@Service
public class TimeTrackerCategoryServiceImpl extends ServiceImpl<ITimeTrackerCategoryMapper, TimeTrackerCategoryEntity> implements ITimeTrackerCategoryService {
}
