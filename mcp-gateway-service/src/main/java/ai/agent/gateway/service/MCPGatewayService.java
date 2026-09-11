package ai.agent.gateway.service;

import ai.agent.gateway.model.MCPTool;
import ai.agent.gateway.model.ToolInfo;
import ai.agent.gateway.registry.MCPToolRegistry;
import ai.agent.gateway.registry.ToolMetadata;
import ai.agent.gateway.security.ToolPermissionManager;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MCPGatewayService {


    private final MCPToolRegistry toolRegistry;

    private final ToolPermissionManager permissionManager;

    /**
     * Spring AI MCP Client提供
     */
    private final ToolCallbackProvider toolCallbackProvider;



    /**
     * 查询所有MCP工具
     */
    public Mono<List<ToolInfo>> listAllTools() {

        return Mono.fromSupplier(() -> {

            syncToolMetadata();

            return toolRegistry.getAllMetadata()
                    .stream()
                    .map(this::convertToToolInfo)
                    .toList();
        }).subscribeOn(
                Schedulers.boundedElastic()
        );


    }



    /**
     * 查询指定Server工具
     */
    public Flux<ToolInfo> getToolsByServer(String serverName) {

        return Mono.fromSupplier(() -> {

                    syncToolMetadata();

                    return toolRegistry.getAllMetadata()
                            .stream()
                            .filter(
                                    item -> serverName.equals(
                                            item.getServerName()
                                    )
                            )
                            .map(this::convertToToolInfo)
                            .toList();

                })
                .flatMapMany(Flux::fromIterable);
    }




    /**
     * Server状态
     */
    public Mono<ServerStatus> getServerStatus(String serverName){

        return Mono.fromSupplier(() -> {

            long count =
                    toolRegistry.getAllMetadata()
                            .stream()
                            .filter(
                                    x -> serverName.equals(
                                            x.getServerName()
                                    )
                            )
                            .count();


            return ServerStatus.builder()
                    .name(serverName)
                    .toolCount((int)count)
                    .connected(count>0)
                    .build();

        });

    }




    /**
     * 所有Server状态
     */
    public Flux<ServerStatus> getAllServerStatus(){

        return Mono.fromSupplier(() -> {

                    return toolRegistry.getAllMetadata()
                            .stream()
                            .collect(
                                    Collectors.groupingBy(
                                            ToolMetadata::getServerName,
                                            Collectors.counting()
                                    )
                            )
                            .entrySet()
                            .stream()
                            .map(entry ->
                                    ServerStatus.builder()
                                            .name(entry.getKey())
                                            .toolCount(entry.getValue().intValue())
                                            .connected(true)
                                            .build()
                            )
                            .toList();

                })
                .flatMapMany(Flux::fromIterable);

    }




    /**
     * 同步Spring AI MCP Client发现的工具
     */
    private void syncToolMetadata(){

        ToolCallback[] callbacks = toolCallbackProvider.getToolCallbacks();


        for(ToolCallback callback:callbacks){

            ToolDefinition definition = callback.getToolDefinition();


            if(!toolRegistry.hasTool(definition.name())){


                MCPTool tool = MCPTool.builder()
                                .name(definition.name())
                                .description(definition.description())
                                .inputSchema(Map.of("schema", definition.inputSchema()))
                                .build();

                toolRegistry.registerTool("mcp-server", tool);

            }

        }

    }




    private ToolInfo convertToToolInfo(ToolMetadata metadata){

        MCPTool tool = toolRegistry.getTool(metadata.getToolName());


        return ToolInfo.builder()
                .name(metadata.getToolName())
                .description(tool==null? null: tool.getDescription())
                .inputSchema(tool==null? Map.of(): tool.getInputSchema())
                .serverName(metadata.getServerName())
                .requiresPermission(permissionManager.requiresPermission(metadata.getToolName()))
                .permissionKey(permissionManager.getRequiredPermission(metadata.getToolName()))
                .build();

    }





    @Data
    @Builder
    public static class ServerStatus{

        private String name;

        private String url;

        private boolean connected;

        private int toolCount;
    }

}