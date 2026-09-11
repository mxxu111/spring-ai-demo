package ai.agent.tool;

import ai.agent.tool.Tool;

import java.util.Collection;

/**
 * Agent 工具注册中心
 */
public interface ToolRegistry {

    /**
     * 根据工具名称获取工具
     */
    Tool getTool(String name);

    /**
     * 获取所有工具
     */
    Collection<Tool> getTools();

    /**
     * 注册工具
     */
    void register(Tool tool);

    /**
     * 删除工具
     */
    void unregister(String name);
}