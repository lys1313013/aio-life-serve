package top.aiolife.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/13 14:30
 */
@Data
public class BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long updateUser;

    @TableLogic
    private Integer isDeleted;

    public void setCreateCommonField(Long userId) {
        this.createUser = userId;
        this.updateUser = userId;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.isDeleted = 0;
    }

    public void setUpdateCommonField(Long userId) {
        this.updateUser = userId;
        this.updateTime = LocalDateTime.now();
    }
}
