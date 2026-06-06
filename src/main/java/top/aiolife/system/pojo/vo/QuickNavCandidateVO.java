package top.aiolife.system.pojo.vo;

import lombok.Data;

/**
 * 快捷导航候选菜单返回对象（用于"添加"弹窗中的菜单树）
 *
 * @author Ethan
 * @date 2026/06/05
 */
@Data
public class QuickNavCandidateVO {

    private Long menuId;

    private String title;

    private String icon;

    private String color;

    private String path;

    /** "self" 内链 / "blank" 外链（取自 meta.link） */
    private String target;

    /** 所属父菜单的标题（用于弹窗内分组） */
    private String parentTitle;
}
