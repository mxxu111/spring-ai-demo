package ai;


import ai.tool.DateTool;
import ai.tool.EmailTool;
import ai.tool.FileTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }

    /**
     * 注册自定义 MCP 工具
     * 这样 ChatClient 就能感知到这些工具的存在
     */
    @Bean
    public ToolCallbackProvider registerMCPTools(DateTool dateTool, EmailTool emailTool, FileTool fileTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(dateTool, emailTool, fileTool)
                .build();
    }

}
