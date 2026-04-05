package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.aiolife.record.mapper.IGoalMapper;
import top.aiolife.record.pojo.entity.GoalEntity;
import top.aiolife.record.service.IGoalService;

/**
 * 目标服务实现类
 *
 * @author Lys
 * @date 2026-04-05
 */
@Service
public class GoalServiceImpl extends ServiceImpl<IGoalMapper, GoalEntity> implements IGoalService {
}
