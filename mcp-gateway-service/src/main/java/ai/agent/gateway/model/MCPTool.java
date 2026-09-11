package ai.agent.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * MCP tool model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MCPTool {

    /**
     * Tool name.
     */
    private String name;

    /**
     * Tool description.
     */
    private String description;

    /**
     * JSON-schema-like argument definition.
     */
    private Map<String, Object> inputSchema;
}
