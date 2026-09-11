
-- Agent 任务表
CREATE TABLE IF NOT EXISTS  agent_task(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id VARCHAR(64) NOT NULL,
    input TEXT,
    goal VARCHAR(500),
    agent_type VARCHAR(50),
    status VARCHAR(20),
    result TEXT,
    create_time DATETIME,
    update_time DATETIME,
    UNIQUE KEY uk_task_id(task_id));

-- Agent 步骤表
CREATE TABLE agent_step(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id VARCHAR(64) NOT NULL,
    step_no INT,
    description VARCHAR(500),
    thought TEXT,
    action TEXT,
    observation TEXT,
    status VARCHAR(20),
    create_time DATETIME,
    INDEX idx_task_id(task_id)
);