package top.aiolife.record.pojo.query;

import lombok.Data;

@Data
public class ReadRecordQuery {
    private String title;
    private Integer type;
    private Integer status;
    private Integer current;
    private Integer size;
}
