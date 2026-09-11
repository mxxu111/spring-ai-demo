package ai.agent.react;

import lombok.Data;

import java.util.Map;

/**
 * ReAct Agent 单轮决策结果
 */
@Data
public class AgentDecision {

    /**
     * 决策类型：
     * TOOL_CALL：调用工具
     * FINAL：返回最终答案
     */
    private String type;

    /**
     * Thought：当前决策依据
     */
    private String thought;

    /**
     * Action：工具名称
     */
    private String action;

    /**
     * Action Input：工具参数
     */
    private Map<String, Object> actionInput;

    /**
     * 最终答案
     */
    private String finalAnswer;

    /**
     * 是否为最终答案
     */
    public boolean isFinalAnswer() {
        return "FINAL".equalsIgnoreCase(type);
    }

    /**
     * 是否调用工具
     */
    public boolean isToolCall() {
        return "TOOL_CALL".equalsIgnoreCase(type);
    }
}