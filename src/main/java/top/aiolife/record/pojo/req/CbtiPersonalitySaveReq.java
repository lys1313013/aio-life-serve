package top.aiolife.record.pojo.req;

import lombok.Data;

import java.util.List;

/**
 * CBTI 人格保存请求体
 *
 * @author Ethan
 * @date 2026/04/19
 */
@Data
public class CbtiPersonalitySaveReq {

    private Long id;

    private String code;

    private String name;

    private String motto;

    private String color;

    private List<Integer> vector;

    private String description;

    private List<String> strengths;

    private List<String> weaknesses;

    private String techStack;

    private String spirit;

    private String imageObject;

    private Boolean isSpecial;
}

