package ai.agent.gateway.connection;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class SSEMCPCConnection implements MCPConnection {

    private final McpAsyncClient client;

    private final String serverName;


    @Override
    public Object callTool(String toolName, Map<String,Object> arguments) {
        McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, arguments);
        return client.callTool(request);
    }


    @Override
    public String getServerName(){
        return serverName;
    }

}

