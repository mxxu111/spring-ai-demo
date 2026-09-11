package ai.agent.executor;

import ai.agent.model.AgentContext;
import ai.agent.model.ToolDefinition;

import java.util.List;
import java.util.Map;

/**
 * 实现要点:
 *
 * 集成 Spring AI 的 ToolCallbackProvider
 * 支持动态注册 MCP Server 提供的工具
 * 工具执行结果封装为 JSON 字符串
 */
public interface ToolExecutor {
    /**
     * 执行工具调用
     * @param toolName 工具名称
     * @param arguments 参数
     * @param context 执行上下文
     * @return 执行结果
     */
    String execute(String toolName, Map<String, Object> arguments, AgentContext context);

    /**
     * 注册工具
     */
    void registerTool(ToolDefinition tool);

    /**
     * 获取所有可用工具
     */
    List<ToolDefinition> getAvailableTools();
}
