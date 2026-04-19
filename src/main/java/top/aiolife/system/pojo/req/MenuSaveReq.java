package top.aiolife.system.pojo.req;

import lombok.Data;

import java.util.Map;

/**
 * 系统菜单保存请求体
 *
 * @author Ethan
 * @date 2026/04/19
 */
@Data
public class MenuSaveReq {

    private Long id;

    private Long parentId;

    private String name;

    private String path;

    private String component;

    private String redirect;

    private Map<String, Object> meta;

    private String roles;

    private Integer sort;

    private Integer status;
}

