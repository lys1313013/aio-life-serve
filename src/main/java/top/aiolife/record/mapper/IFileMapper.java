package top.aiolife.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aiolife.record.pojo.entity.FileEntity;

@Mapper
public interface IFileMapper extends BaseMapper<FileEntity> {
}
