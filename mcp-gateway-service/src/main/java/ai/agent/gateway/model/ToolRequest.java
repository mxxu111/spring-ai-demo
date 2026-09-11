package ai.agent.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 工具调用请求模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolRequest {

    /**
     * 工具名称
     */
    private String toolName;

    /**
     * 调用参数
     */
    private Map<String, Object> arguments;

    /**
     * 调用者标识（用于权限和审计）
     */
    private String caller;

    /**
     * 请求ID（用于追踪）
     */
    private String requestId;

}