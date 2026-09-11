package ai.agent.impl;
import ai.agent.Agent;
import ai.agent.model.AgentContext;
import ai.agent.model.AgentResult;
import ai.agent.model.Task;
/**
 * Chat Agent 实现
 * 兼容现有的对话式代理
 */
public class ChatAgent implements Agent {

    @Override
    public AgentResult execute(Task task, AgentContext context){
        String answer = callLLM(task.getInput());
        return AgentResult.builder()
                .success(true)
                .build();
    }



    private String callLLM(String input){
        return "";
    }

    @Override
    public String name(){
        return "chat-agent";
    }


}