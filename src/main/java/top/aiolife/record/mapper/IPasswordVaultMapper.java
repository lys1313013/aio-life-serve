package top.aiolife.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.record.pojo.entity.PasswordVaultEntity;

/**
 * 密码库Mapper
 *
 * @author Lys
 * @date 2026/04/28
 */
@Mapper
public interface IPasswordVaultMapper extends BaseMapper<PasswordVaultEntity> {
}