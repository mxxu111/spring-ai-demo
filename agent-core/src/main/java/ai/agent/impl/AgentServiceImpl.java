package ai.agent.impl;

import ai.agent.Agent;
import ai.agent.executor.AgentExecutor;
import ai.agent.model.AgentContext;
import ai.agent.model.AgentResult;
import ai.agent.model.Task;
import ai.agent.react.ReActAgent;
import ai.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Agent 服务实现
 */
@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentExecutor agentExecutor;

    private final ReActAgent reActAgent;

    @Override
    public AgentResult execute(Task task) {

        AgentContext context = new AgentContext();
        context.setUserInput(task.getInput());
        return execute(task, context);
    }

    @Override
    public AgentResult execute(Task task, AgentContext context) {

        return agentExecutor.execute(reActAgent, task, context);
    }
}