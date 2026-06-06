-- 将 exercise_record 表中的 exercise_type_id 从 dict_value 迁移到 user_dict_data 的 id
-- 通过 COALESCE(udd.template_id, udd.id) 确保使用正确的分类 ID
UPDATE exercise_record er
JOIN user_dict_data udd ON er.exercise_type_id = udd.dict_value 
    AND udd.dict_type = 'exercise_type'
    AND (udd.user_id = 0 OR udd.user_id = er.user_id)
SET er.exercise_type_id = CAST(COALESCE(udd.template_id, udd.id) AS CHAR)
WHERE er.exercise_type_id NOT REGEXP '^[0-9]+$';

-- （可选）修改字段类型为 BIGINT 以规范数据格式，如果代码中实体类保持 String 则可以不修改
-- ALTER TABLE exercise_record MODIFY COLUMN exercise_type_id BIGINT COMMENT '运动类型ID(关联user_dict_data主键)';
