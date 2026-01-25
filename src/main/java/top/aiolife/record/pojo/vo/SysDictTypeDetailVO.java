package top.aiolife.record.pojo.vo;

import top.aiolife.record.pojo.entity.SysDictTypeEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 通用字典返回值
 *
 * @author Lys
 * @date 2025/04/05 22:33
 */
@Getter
@Setter
public class SysDictTypeDetailVO {
    private SysDictTypeEntity sysDictTypeEntity;

    /**
     * 明细数据
     */
    private List<SysDictDataVO> dictDetailList;
}
