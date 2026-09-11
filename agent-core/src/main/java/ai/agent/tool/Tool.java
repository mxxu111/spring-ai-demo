package ai.agent.tool;

import java.util.Map;

/**
 * Agent 可执行工具
 */
public interface Tool {

    /**
     * 获取工具名称
     */
    String name();

    /**
     * 获取工具描述
     */
    String description();

    /**
     * 执行工具
     *
     * @param input 工具输入参数
     * @return 工具执行结果
     */
    String execute(Map<String, Object> input);
}