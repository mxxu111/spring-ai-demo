package ai.agent;

import ai.agent.model.AgentContext;
import ai.agent.model.AgentResult;
import ai.agent.model.Task;

/**
 * Agent 核心接口
 */
public interface Agent {

    /**
     * Agent执行任务
     *
     * @param task 任务
     * @param context 上下文
     * @return 执行结果
     */
    AgentResult execute(Task task, AgentContext context);


    /**
     * 获取Agent名称
     */
    String name();


    /**
     * 获取Agent类型
     */
    default String type(){
        return getClass().getSimpleName();
    }


    /**
     * 是否支持任务
     */
    default boolean support(Task task){
        return true;
    }

}
