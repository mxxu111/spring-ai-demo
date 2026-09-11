package ai.agent.gateway.registry;

import ai.agent.gateway.model.MCPTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * MCP工具元数据注册中心
 *
 * <p>负责管理：
 * <ul>
 *     <li>MCP工具信息</li>
 *     <li>工具所属Server</li>
 *     <li>工具运行状态</li>
 *     <li>工具统计信息</li>
 * </ul>
 *
 * <p>不负责：
 * <ul>
 *     <li>MCP协议通信</li>
 *     <li>Tool调用</li>
 *     <li>ToolCallback管理</li>
 * </ul>
 */
@Slf4j
@Component

/**
 * 保存tools/list结果
 */

public class MCPToolRegistry {


    /**
     * 工具信息
     */
    private final Map<String, MCPTool> tools = new ConcurrentHashMap<>();
    /**
     * 工具运行元数据
     */
    private final Map<String, ToolMetadata> metadata = new ConcurrentHashMap<>();
    /**
     * 工具所属MCP Server
     */
    private final Map<String, String> toolToServer = new ConcurrentHashMap<>();
    /**
     * Server工具数量统计
     */
    private final Map<String, Integer> serverToolCount = new ConcurrentHashMap<>();

    /**
     * 注册单个工具
     */
    public void registerTool(String serverName, MCPTool tool){
        Objects.requireNonNull(serverName, "serverName must not be null");
        Objects.requireNonNull(tool, "tool must not be null");
        String toolName = tool.getName();
        String oldServer = toolToServer.put(toolName, serverName);
        tools.put(toolName, tool);
        ToolMetadata oldMetadata = metadata.get(toolName);


        ToolMetadata toolMetadata = ToolMetadata.builder()
                        .toolName(toolName)
                        .serverName(serverName)
                        .available(true)
                        .registeredAt(oldMetadata != null ? oldMetadata.getRegisteredAt() : LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .invokeCount(oldMetadata != null ? oldMetadata.getInvokeCount() : 0L)
                        .lastInvokedAt(oldMetadata != null ? oldMetadata.getLastInvokedAt() : null)
                        .build();


        metadata.put(toolName, toolMetadata);


        // Server数量统计
        if(oldServer == null){

            serverToolCount.merge(serverName, 1, Integer::sum);

        }else if(!oldServer.equals(serverName)){

            decrementServerToolCount(oldServer);

            serverToolCount.merge(serverName, 1, Integer::sum);
        }


        log.debug("Registered MCP tool={}, server={}", toolName, serverName);
    }




    /**
     * 批量注册工具
     */
    public void registerTools(String serverName, List<MCPTool> toolList){

        Objects.requireNonNull(toolList, "toolList must not be null");
        unregisterToolsByServer(serverName);
        toolList.forEach(tool -> registerTool(serverName, tool));

        log.info("Registered {} tools from server {}", toolList.size(), serverName);
    }





    /**
     * 删除工具
     */
    public void unregisterTool(String toolName){

        String serverName = toolToServer.remove(toolName);


        tools.remove(toolName);

        metadata.remove(toolName);


        if(serverName != null){
            decrementServerToolCount(serverName);
        }


        log.debug("Unregistered tool {}", toolName
        );
    }





    /**
     * 删除指定Server全部工具
     */
    public void unregisterToolsByServer(String serverName){

        List<String> removeList = new ArrayList<>();

        toolToServer.forEach(
                (tool,server)->{
                    if(serverName.equals(server)){
                        removeList.add(tool);
                    }
                }
        );

        removeList.forEach(this::unregisterTool);

        log.info("Removed {} tools from server {}", removeList.size(), serverName);
    }

    public MCPTool getTool(String toolName){
        return tools.get(toolName);
    }

    public ToolMetadata getMetadata(String toolName){
        return metadata.get(toolName);
    }


    public String getToolServer(String toolName){
        return toolToServer.get(toolName);
    }





    public Collection<MCPTool> getAllTools(){

        return List.copyOf(tools.values());
    }




    public Collection<ToolMetadata> getAllMetadata(){

        return List.copyOf(metadata.values());
    }





    public int getToolCountByServer(String serverName){

        return serverToolCount.getOrDefault(serverName, 0);
    }




    public boolean hasTool(String toolName){

        return tools.containsKey(toolName);
    }




    /**
     * 修改工具状态
     */
    public void setToolAvailable(String toolName, boolean available){

        ToolMetadata toolMetadata = metadata.get(toolName);


        if(toolMetadata != null){

            toolMetadata.setAvailable(available);

            toolMetadata.setUpdatedAt(LocalDateTime.now());
        }
    }





    public int getToolCount(){
        return tools.size();
    }



    public void clear(){
        tools.clear();
        metadata.clear();
        toolToServer.clear();
        serverToolCount.clear();

        log.info("Cleared all MCP tools");
    }





    private void decrementServerToolCount(String serverName){

        serverToolCount.computeIfPresent(
                serverName,
                (key,count)->
                        count <= 1 ?
                                null :
                                count - 1
        );
    }

}