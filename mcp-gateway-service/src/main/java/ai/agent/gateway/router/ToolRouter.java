package ai.agent.gateway.router;

import ai.agent.gateway.connection.MCPConnection;
import ai.agent.gateway.connection.MCPConnectionManager;
import ai.agent.gateway.exception.MCPGatewayException;
import ai.agent.gateway.model.MCPTool;
import ai.agent.gateway.registry.MCPToolRegistry;
import ai.agent.gateway.registry.ToolMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * 工具路由器
 *
 * <p>负责工具调用的路由和执行：
 * <ul>
 *     <li>检查工具是否存在和可用</li>
 *     <li>路由工具调用到正确的执行器</li>
 *     <li>提供工具信息查询</li>
 * </ul>
 *
 * <p>设计说明：
 * <ul>
 *     <li>工具执行由 Spring AI MCP Client 负责</li>
 *     <li>本类提供工具查询和路由逻辑</li>
 *     <li>与 ToolInvokeService 配合完成调用流程</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolRouter {


    private final MCPToolRegistry registry;


    private final MCPConnectionManager connectionManager;



    public Object route(String toolName, Map<String,Object> args){


        //1. 查询工具
        MCPTool tool = registry.getTool(toolName);

        if(tool==null){
            throw new MCPGatewayException("Tool不存在:"+toolName);
        }


        //2. 查询所属Server
        String serverName = registry.getToolServer(toolName);


        //3. 获取连接
        MCPConnection connection = connectionManager.get(serverName);

        if(connection==null){
            throw new MCPGatewayException("MCP Server不存在:"+serverName);

        }

        //4. 调用
        return connection.callTool(toolName, args);

    }

}
