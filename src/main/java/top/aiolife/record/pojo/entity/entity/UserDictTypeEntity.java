package top.aiolife.record.pojo.entity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.aiolife.record.pojo.entity.BaseEntity;

/**
 * 用户字典类型实体类
 *
 * @author Lys
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_dict_type")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDictTypeEntity extends BaseEntity {

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
