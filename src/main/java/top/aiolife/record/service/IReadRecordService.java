package top.aiolife.record.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.record.pojo.entity.ReadRecordEntity;
import top.aiolife.record.pojo.query.ReadRecordQuery;
import top.aiolife.record.pojo.req.ReadRecordReq;
import top.aiolife.record.pojo.vo.ReadRecordVO;

public interface IReadRecordService extends IService<ReadRecordEntity> {
    Page<ReadRecordVO> pageList(ReadRecordQuery query);

    void saveRecord(ReadRecordReq req);

    void updateRecord(ReadRecordReq req);

    void deleteRecord(Long id);

    ReadRecordReq parseDouban(String url);

    java.util.List<ReadRecordVO> listActive();

    ReadRecordVO getVOById(Long id);
}
