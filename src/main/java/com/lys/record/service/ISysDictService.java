package com.lys.record.service;

import com.lys.record.pojo.entity.SysDictDataEntity;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/03 14:24
 */
public interface ISysDictService {

    List<SysDictDataEntity> getDictDataByDictType(String dictType);
}
