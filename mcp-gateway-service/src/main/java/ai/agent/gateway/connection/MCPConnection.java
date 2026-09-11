package ai.agent.gateway.connection;



import java.util.Map;


public interface MCPConnection {


    /**
     * MCP Server名称
     */
    String getServerName();



    /**
     * 调用工具
     */
    Object callTool(String toolName, Map<String,Object> arguments);



}
