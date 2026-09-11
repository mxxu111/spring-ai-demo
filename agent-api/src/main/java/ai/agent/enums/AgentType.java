package ai.agent.enums;

import lombok.Getter;

/**
 * Agent 类型枚举
 */
@Getter
public enum AgentType {

    /**
     * ReAct 模式：Thought → Action → Observation 循环
     */
    REACT("ReAct Agent", "基于 Thought-Action-Observation 循环的推理执行模式"),

    /**
     * Plan-Execute 模式：先规划再执行
     */
    PLAN_EXECUTE("Plan-Execute Agent", "先制定完整计划，再依次执行各个步骤"),

    /**
     * Chat 模式：普通对话（兼容现有模式）
     */
    CHAT("Chat Agent", "普通的对话模式，单次工具调用"),

    /**
     * Multi-Agent 模式：多 Agent 协作
     */
    MULTI_AGENT("Multi-Agent", "多个 Agent 协作完成复杂任务");

    private final String name;
    private final String description;

    AgentType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
