package ai.agent.model;

import lombok.Data;

/**
 * ReAct Agent 单次执行步骤
 *
 * <p>
 * 对应一次：
 * Thought → Action → Observation
 * </p>
 */
@Data
public class AgentStep {

    /**
     * Thought：LLM 当前的决策依据
     */
    private String thought;

    /**
     * Action：调用的工具名称
     */
    private String action;

    /**
     * Action Input：工具输入参数
     */
    private String actionInput;

    /**
     * Observation：工具执行结果
     */
    private String observation;

    /**
     * Final Answer：最终答案
     */
    private String finalAnswer;
}