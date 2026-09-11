package ai.service;

import ai.agent.model.AgentContext;
import ai.agent.model.AgentResult;
import ai.agent.model.Task;

/**
 * Agent 服务
 *
 * 对外提供 Agent 任务执行能力。
 */
public interface AgentService {

    /**
     * 执行 Agent 任务
     *
     * @param task 任务
     * @return Agent执行结果
     */
    AgentResult execute(Task task);

    /**
     * 执行 Agent 任务
     *
     * @param task    任务
     * @param context Agent上下文
     * @return Agent执行结果
     */
    AgentResult execute(Task task, AgentContext context);
}