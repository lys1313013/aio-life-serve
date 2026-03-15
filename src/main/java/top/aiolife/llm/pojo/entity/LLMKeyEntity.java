package top.aiolife.llm.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("llm_key")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LLMKeyEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * API密钥（加密存储）
     */
    private String apiKey;

    /**
     * 基础URL
     */
    private String baseUrl;

    /**
     * 是否默认
     */
    private Integer isDefault;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @JsonSetter
    public void setIsDefault(Object value) {
        if (value instanceof Boolean) {
            this.isDefault = ((Boolean) value) ? 1 : 0;
        } else if (value instanceof Integer) {
            this.isDefault = (Integer) value;
        } else if (value instanceof String) {
            this.isDefault = Boolean.parseBoolean((String) value) ? 1 : 0;
        }
    }
}
