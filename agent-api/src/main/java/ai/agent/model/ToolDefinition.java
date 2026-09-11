package ai.agent.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ToolDefinition {
    private String name;              // 工具名称,如 "search"
    private String description;       // 工具描述
    private Map<String, Object> parametersSchema;  // JSON Schema 格式的参数定义
    private String type = "function"; // 工具类型
}
