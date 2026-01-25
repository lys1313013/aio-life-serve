package top.aiolife.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 字典数据实体
 *
 * @author Lys
 * @date 2025/04/06 00:33
 */
@Getter
@Setter
@TableName("sys_dict_data")
public class SysDictDataEntity {

    /**
     * 字典代码，作为主键
     */
    @TableId(type = IdType.AUTO)
    private Integer dictCode;
    /**
     * 字典ID
     */
    private Integer dictId;

    /**
     * 字典名称
     */
    @TableField(exist = false)
    private String dictName;

    /**
     * 字典类型
     */
    @TableField(exist = false)
    private String dictType;

    /**
     * 字典排序
     */
    private Integer dictSort;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * CSS类名
     */
    private String cssClass;

    /**
     * 列表类名
     */
    private String listClass;

    /**
     * 是否为默认值
     */
    private String isDefault;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
}
