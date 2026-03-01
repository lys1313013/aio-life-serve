package top.aiolife.sso.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.sso.pojo.entity.MailLogEntity;

/**
 * 邮件发送记录 Mapper 接口
 *
 * @author Lys
 * @date 2026/03/01
 */
@Mapper
public interface MailLogMapper extends BaseMapper<MailLogEntity> {
}
