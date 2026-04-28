package top.aiolife.sso.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.sso.pojo.entity.PasswordVaultEntity;

/**
 * 密码库 Mapper
 *
 * @author Lys
 * @date 2026-04-28
 */
@Mapper
public interface PasswordVaultMapper extends BaseMapper<PasswordVaultEntity> {
}