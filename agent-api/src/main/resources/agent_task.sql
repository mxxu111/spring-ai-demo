CREATE TABLE agent_task (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            task_id VARCHAR(64) UNIQUE NOT NULL COMMENT '任务ID',
                            user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
                            goal TEXT NOT NULL COMMENT '任务目标',
                            task_type VARCHAR(32) COMMENT '任务类型',
                            status VARCHAR(32) NOT NULL COMMENT '状态：PENDING/PLANNING/EXECUTING/COMPLETED/FAILED',
                            plan TEXT COMMENT '执行计划',
                            result TEXT COMMENT '最终结果',
                            error_message TEXT COMMENT '错误信息',
                            priority INT DEFAULT 0 COMMENT '优先级',
                            max_iterations INT DEFAULT 10 COMMENT '最大迭代次数',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            started_at TIMESTAMP NULL COMMENT '开始时间',
                            completed_at TIMESTAMP NULL COMMENT '完成时间',
                            INDEX idx_user_id (user_id),
                            INDEX idx_status (status)
) COMMENT 'Agent 任务表';