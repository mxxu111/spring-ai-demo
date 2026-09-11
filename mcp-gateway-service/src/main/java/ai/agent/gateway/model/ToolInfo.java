package ai.agent.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 工具信息模型
 * 用于对外展示工具的详细信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolInfo {

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具描述
     */
    private String description;

    /**
     * 输入参数 Schema (JSON Schema 格式)
     */
    private Map<String, Object> inputSchema;

    /**
     * 所属 MCP 服务器名称
     */
    private String serverName;

    /**
     * 是否需要权限
     */
    private boolean requiresPermission;

    /**
     * 权限标识
     */
    private String permissionKey;

    /**
     * 工具版本
     */
    private String version;

    /**
     * 工具分类
     */
    private String category;

}