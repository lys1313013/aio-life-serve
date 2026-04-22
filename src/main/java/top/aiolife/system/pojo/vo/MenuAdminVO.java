package top.aiolife.system.pojo.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 菜单管理端返回对象
 *
 * @author Ethan
 * @date 2026/04/19
 */
@Data
public class MenuAdminVO {

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

    private List<MenuAdminVO> children;
}

