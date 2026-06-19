ALTER TABLE time_record 
ADD COLUMN relate_id BIGINT COMMENT '关联业务ID（阅读记录/观影记录等的主键ID）',
ADD COLUMN relate_type TINYINT COMMENT '关联业务类型：1-阅读，2-观影';
