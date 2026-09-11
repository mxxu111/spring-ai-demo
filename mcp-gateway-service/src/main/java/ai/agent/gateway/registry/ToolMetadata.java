package ai.agent.gateway.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Runtime metadata for a registered MCP tool.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolMetadata {

    private String toolName;
    private String serverName;
    private String serverUrl;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private Long invokeCount;
    private LocalDateTime lastInvokedAt;
    private boolean available;

    public synchronized void incrementInvokeCount() {
        if (this.invokeCount == null) {
            this.invokeCount = 0L;
        }
        this.invokeCount++;
        this.lastInvokedAt = LocalDateTime.now();
        this.updatedAt = this.lastInvokedAt;
    }
}
