package ai.agent.executor;

import ai.agent.Agent;
import ai.agent.model.AgentContext;
import ai.agent.model.AgentResult;
import ai.agent.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Agent 执行器
 *
 * <p>
 * 负责统一调度 Agent 执行任务，
 * 并处理 Agent 执行过程中产生的异常。
 *
 * <p>
 * 当前 ReAct 循环由具体的 Agent（例如 ReActAgent）负责。
 */
@Component
@RequiredArgsConstructor
public class AgentExecutor {

    /**
     * 执行 Agent
     *
     * @param agent   Agent
     * @param task    待执行任务
     * @param context Agent 执行上下文
     * @return Agent 执行结果
     */
    public AgentResult execute(Agent agent, Task task, AgentContext context) {

        try {

            return agent.execute(task, context);

        } catch (Exception e) {

            return AgentResult.builder().success(false).errorMessage(e.getMessage()).build();
        }
    }
}