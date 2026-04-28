package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.aiolife.record.mapper.IPasswordVaultMapper;
import top.aiolife.record.pojo.entity.PasswordVaultEntity;
import top.aiolife.record.service.IPasswordVaultService;

/**
 * 密码库Service实现
 *
 * @author Lys
 * @date 2026/04/28
 */
@Service
public class PasswordVaultServiceImpl extends ServiceImpl<IPasswordVaultMapper, PasswordVaultEntity> implements IPasswordVaultService {
}