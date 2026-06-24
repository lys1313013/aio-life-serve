package top.aiolife.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("read_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReadRecordEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String title;
    
    private Integer type;
    
    private String author;
    
    private String url;
    
    /**
     * 图片文件ID
     */
    private String fileId;

    /**
     * 临时图片链接，仅供前端显示，不持久化
     */
    @TableField(exist = false)
    private String coverImgUrl;
    
    private Integer status;
    
    private Integer totalProgress;
    
    private Integer currentProgress;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;
    
    private String remark;

    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

    @TableLogic
    private Integer isDeleted;
}
