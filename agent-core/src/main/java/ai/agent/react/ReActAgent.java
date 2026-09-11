package ai.agent.react;

import ai.agent.Agent;
import ai.agent.model.AgentContext;
import ai.agent.model.AgentResult;
import ai.agent.model.AgentStep;
import ai.agent.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * ReAct Agent
 *
 * <p>
 * 基于 ReAct 模式执行任务：
 *
 * <pre>
 * Thought
 *    ↓
 * Action
 *    ↓
 * Tool
 *    ↓
 * Observation
 *    ↓
 * Thought
 *    ↓
 * Final Answer
 * </pre>
 */
@Component
@Slf4j
public class ReActAgent implements Agent {

    /**
     * Agent名称
     */
    private static final String NAME = "react-agent";

    /**
     * 最大执行轮数
     */
    private static final int MAX_STEPS = 5;

    private final ChatClient chatClient;

    /**
     * MCP / Spring AI Tool
     */
    private final ToolCallback[] toolCallbacks;


    public ReActAgent(ChatClient.Builder chatClientBuilder, @Autowired(required = false) ToolCallbackProvider toolCallbackProvider, ChatMemory chatMemory) {

        ChatClient.Builder builder = chatClientBuilder;

        if (toolCallbackProvider != null) {

            this.toolCallbacks = toolCallbackProvider.getToolCallbacks();

            log.info("ReActAgent加载MCP Tools：{}", Arrays.stream(toolCallbacks)
                            .map(t -> t.getToolDefinition().name())
                            .toList());

        } else {

            this.toolCallbacks = new ToolCallback[0];

            log.warn("ReActAgent未发现 ToolCallbackProvider");
        }

        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }


    /**
     * 执行 ReAct Agent
     */
    @Override
    public AgentResult execute(Task task, AgentContext context) {

        while (context.getStep() < MAX_STEPS) {

            context.setStep(context.getStep() + 1);

            log.info("ReAct Step={}, task={}", context.getStep(), task.getInput());

            /*
             * 1. LLM 推理
             */
            AgentDecision decision = think(task, context);

            log.info("AgentDecision type={}, action={}", decision.getType(), decision.getAction());


            /*
             * 2. FINAL
             */
            if (decision.isFinalAnswer()) {

                context.setFinished(true);

                context.setFinalAnswer(decision.getFinalAnswer());
                return AgentResult.success(decision.getFinalAnswer(), context.getSteps());
            }


            /*
             * 3. TOOL_CALL
             */
            if (!decision.isToolCall()) {
                return AgentResult.failure("LLM返回未知决策类型：" + decision.getType());
            }


            /*
             * 4. Action
             */
            String observation = executeAction(decision);


            /*
             * 5. 保存Step
             */
            AgentStep step = new AgentStep();
            step.setThought(decision.getThought());
            step.setAction(decision.getAction());
            step.setActionInput(String.valueOf(decision.getActionInput()));
            step.setObservation(observation);
            context.getSteps().add(step);
            log.info("Observation={}", observation);
        }
        return AgentResult.failure("Agent执行超过最大步骤数：" + MAX_STEPS);
    }


    /**
     * LLM推理
     */
    private AgentDecision think(Task task, AgentContext context) {

        String prompt = buildPrompt(task, context);

        return chatClient
                .prompt()
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, task.getTaskId()))
                .call()
                .entity(AgentDecision.class);
    }


    /**
     * 执行 Tool
     */
    private String executeAction(AgentDecision decision) {

        String action = decision.getAction();

        if (action == null || action.isBlank()) {
            return "Tool执行失败：action为空";
        }


        /*
         * 根据名称查找 MCP Tool
         */
        ToolCallback callback = findTool(action);

        if (callback == null) {
            return "Tool不存在：" + action;
        }


        try {

            Object input = decision.getActionInput();

            String jsonInput = input == null ? "{}" : input.toString();

            return callback.call(jsonInput);

        } catch (Exception e) {

            log.error("Tool执行失败，tool={}", action,e);

            return "Tool执行失败：" + e.getMessage();
        }
    }


    /**
     * 根据Tool名称查找ToolCallback
     */
    private ToolCallback findTool(String name) {

        for (ToolCallback callback : toolCallbacks) {

            String toolName = callback.getToolDefinition().name();

            if (name.equals(toolName)) {

                return callback;
            }
        }

        return null;
    }


    /**
     * 构建 ReAct Prompt
     */
    private String buildPrompt(Task task, AgentContext context) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                
                你是一个 ReAct Agent。
                
                你的任务是完成用户的问题。
                
                你可以调用下面的工具：
                
                """);


        /*
         * 把MCP工具列表告诉LLM
         */
        for (ToolCallback callback :toolCallbacks) {

            var definition =callback.getToolDefinition();

            prompt.append("""
                    
                    工具名称：%s
                    工具描述：%s
                    
                    """.formatted(
                    definition.name(),
                    definition.description()
            ));
        }


        prompt.append("""
                
                决策规则：
                
                如果需要调用工具：
                
                type = TOOL_CALL
                
                如果已经获得足够的信息：
                
                type = FINAL
                
                返回 JSON：
                
                {
                  "type": "TOOL_CALL",
                  "thought": "调用工具的原因",
                  "action": "工具名称",
                  "actionInput": {},
                  "finalAnswer": null
                }
                
                或：
                
                {
                  "type": "FINAL",
                  "thought": "最终判断",
                  "action": null,
                  "actionInput": null,
                  "finalAnswer": "最终答案"
                }
                
                用户任务：
                
                """);

        prompt.append(task.getInput());
        prompt.append("\n\n历史执行记录：\n");


        if (context.getSteps() == null || context.getSteps().isEmpty()) {
            prompt.append("暂无历史执行记录");

        } else {

            for (AgentStep step :context.getSteps()) {
                prompt.append("""
                        
                        Thought: %s
                        Action: %s
                        Action Input: %s
                        Observation: %s
                        
                        """.formatted(
                        step.getThought(),
                        step.getAction(),
                        step.getActionInput(),
                        step.getObservation()
                ));
            }
        }


        prompt.append("""
                
                请根据以上信息决定下一步。
                
                如果 Observation 已经包含
                用户问题所需要的信息，
                必须返回 FINAL。
                
                不要重复调用已经失败的工具。
                
                """);

        return prompt.toString();
    }


    @Override
    public String name() {
        return NAME;
    }
}