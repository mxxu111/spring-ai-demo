package ai.agent.gateway.controller;

import ai.agent.gateway.model.ToolInfo;
import ai.agent.gateway.service.MCPGatewayService;
import ai.agent.gateway.service.MCPGatewayService.ServerStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/mcp/gateway")
@RequiredArgsConstructor
/**
 * 接收上游MCP请求
 */
public class MCPGatewayController {


    private final MCPGatewayService gatewayService;



    /**
     * 查询所有MCP工具
     *
     * GET /mcp/gateway/tools
     */
    @GetMapping("/tools")
    public Mono<List<ToolInfo>> listTools(){
        return gatewayService.listAllTools();

    }




    /**
     * 查询指定Server工具
     *
     * GET /mcp/gateway/server/{server}/tools
     */
    @GetMapping("/server/{serverName}/tools")
    public Flux<ToolInfo> getServerTools(@PathVariable String serverName){
        return gatewayService.getToolsByServer(serverName);

    }




    /**
     * 查询Server状态
     *
     * GET /mcp/gateway/server/{server}/status
     */
    @GetMapping("/server/{serverName}/status")
    public Mono<ServerStatus> serverStatus(@PathVariable String serverName){
        return gatewayService.getServerStatus(serverName);
    }




    /**
     * 查询所有Server状态
     *
     * GET /mcp/gateway/servers/status
     */
    @GetMapping("/servers/status")
    public Flux<ServerStatus> allStatus(){
        return gatewayService.getAllServerStatus();

    }

}