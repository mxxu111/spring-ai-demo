package ai.agent.gateway.callback;

import ai.agent.gateway.model.MCPTool;
import ai.agent.gateway.router.ToolRouter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.util.Map;


/**
 * 把一个 MCP Tool 包装成 Spring AI ToolCallback。
 * 例如 Registry 里面：
 *
 * {
 *  name:"queryOrder",
 *  description:"查询订单"
 * }
 *
 * 转换成：
 *
 * ToolCallback
 *
 * Spring AI MCP Server 才认识。
 *
 *
 * 调用链：
 *
 * LLM
 *  |
 *  |
 * tools/call
 *  |
 *  |
 * MCPGatewayToolCallback
 *  |
 *  |
 * ToolRouter
 *  |
 *  |
 * MCPConnection
 *  |
 *  |
 * order-mcp-server
 */
@Slf4j
public class MCPGatewayToolCallback implements ToolCallback {


    private final MCPTool tool;

    private final ToolRouter toolRouter;

    private final ObjectMapper objectMapper;



    public MCPGatewayToolCallback(MCPTool tool, ToolRouter toolRouter, ObjectMapper objectMapper){
        this.tool = tool;
        this.toolRouter = toolRouter;
        this.objectMapper = objectMapper;
    }



    @Override
    public ToolDefinition getToolDefinition() {

        try {
            return ToolDefinition.builder()
                    .name(tool.getName())
                    .description(tool.getDescription())
                    .inputSchema(objectMapper.writeValueAsString(tool.getInputSchema()))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }



    @Override
    public String call(String input){
        try {
            Map<String,Object> args = objectMapper.readValue(input, new TypeReference<Map<String,Object>>() {});
            Object result = toolRouter.route(tool.getName(), args);
            log.info("tool={}, result={}", tool.getName(), result);
            return objectMapper.writeValueAsString(result);
        }catch(Exception e){
            log.info("tool.getName()");
            throw new RuntimeException(e);

        }

    }

}
