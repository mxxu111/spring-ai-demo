package ai.agent.model;

import ai.agent.enums.AgentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 任务定义
 *
 * 定义 Agent 需要完成的目标。
 */
/**
 * 用户任务
 */
@Data
public class Task {


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
     * 创建时间
     */
    private long createTime;


}
