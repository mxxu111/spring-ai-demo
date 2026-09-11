package ai.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class DateTool {

    // @Tool 注解将方法暴露为 MCP 工具
    // description 非常重要，大模型根据它来判断何时调用此工具
    @Tool(description = "获取当前时间")
    public String getCurrentTime() {
        log.info("=================调用MCP工具：获取当前时间=================");
        return String.format("当前的时间是 %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

}


