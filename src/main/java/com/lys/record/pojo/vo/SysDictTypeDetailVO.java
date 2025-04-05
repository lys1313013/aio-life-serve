package com.lys.record.pojo.vo;

import com.lys.record.pojo.entity.SysDictDataEntity;
import com.lys.record.pojo.entity.SysDictTypeEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/05 22:33
 */
@Getter
@Setter
public class SysDictTypeDetailVO {
    private SysDictTypeEntity sysDictTypeEntity;
    private List<SysDictDataEntity> sysDictDataEntityList;
}
