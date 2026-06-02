package top.aiolife.relationship.service;

import top.aiolife.relationship.pojo.entity.PersonRelationship;
import top.aiolife.relationship.pojo.entity.PersonWithRelationships;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 人物 Service 接口
 */
public interface IPersonService {

    /**
     * 获取用户的所有人物
     */
    List<PersonRelationship> getAllPersons(Long userId);

    /**
     * 根据ID获取人物
     */
    Optional<PersonRelationship> getPersonById(Long userId, String personId);

    /**
     * 搜索人物
     */
    List<PersonRelationship> searchPersons(Long userId, String keyword);

    /**
     * 创建人物
     */
    PersonRelationship createPerson(PersonRelationship person);

    /**
     * 更新人物
     */
    PersonRelationship updatePerson(Long userId, PersonRelationship person);

    /**
     * 删除人物
     */
    void deletePerson(Long userId, String personId);

    /**
     * 获取图谱数据（节点和边）
     */
    Map<String, Object> getGraphData(Long userId);

    /**
     * 获取人物详情（包含关系）
     */
    PersonWithRelationships getPersonWithRelationships(Long userId, String personId);
}
