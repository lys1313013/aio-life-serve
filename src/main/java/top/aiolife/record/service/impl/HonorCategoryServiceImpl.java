package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.aiolife.record.mapper.IHonorCategoryMapper;
import top.aiolife.record.pojo.entity.HonorCategoryEntity;
import top.aiolife.record.service.IHonorCategoryService;

/**
 * 荣誉分类服务实现
 *
 * @author Lys
 * @date 2026/04/11
 */
@Service
public class HonorCategoryServiceImpl extends ServiceImpl<IHonorCategoryMapper, HonorCategoryEntity> implements IHonorCategoryService {
}
