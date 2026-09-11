package ai.agent.entity;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * 任务实体
 * 表示一个 Agent 任务
 */

@Data
public class TaskEntity {


    /**
     * 主键
     */
    private Long id;


    /**
     * 任务ID
     */
    private String taskId;


    /**
     * 用户输入
     */
    private String input;


    /**
     * 任务目标
     */
    private String goal;


    /**
     * Agent类型
     */
    private String agentType;


    /**
     * 状态
     *
     * CREATED
     * RUNNING
     * SUCCESS
     * FAILED
     */
    private String status;


    /**
     * 执行结果
     */
    private String result;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}