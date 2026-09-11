CREATE TABLE agent_memory (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
                              session_id VARCHAR(64) COMMENT '会话ID',
                              memory_type VARCHAR(32) COMMENT '记忆类型：SHORT_TERM/LONG_TERM',
                              content TEXT NOT NULL COMMENT '记忆内容',
                              importance INT DEFAULT 0 COMMENT '重要程度',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              last_accessed_at TIMESTAMP COMMENT '最后访问时间',
                              access_count INT DEFAULT 0 COMMENT '访问次数',
                              INDEX idx_user_session (user_id, session_id)
) COMMENT 'Agent 记忆表';