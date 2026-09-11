package ai.agent.gateway.config;

import ai.agent.gateway.callback.MCPGatewayToolCallback;
import ai.agent.gateway.registry.MCPToolRegistry;
import ai.agent.gateway.router.ToolRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring 配置类，负责把 MCP Tool 注册成 Spring Bean。
 */
@Configuration
public class GatewayToolConfig {


    @Bean
    @Lazy
    public List<ToolCallback> tools(MCPToolRegistry registry, ToolRouter router, ObjectMapper objectMapper){

        return registry.getAllTools()
                .stream()
                .map(tool -> new MCPGatewayToolCallback(tool, router,objectMapper))
                .collect(Collectors.toList());

    }

}
