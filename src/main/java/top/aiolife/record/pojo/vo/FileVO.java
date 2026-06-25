package top.aiolife.record.pojo.vo;

import lombok.Data;

@Data
public class FileVO {

    private String id;

    private String fileName;

    private Long fileSize;

    private String fileType;

    private String fileUrl;
}
