package ai.agent.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "agent")
public class AgentProperties {


    /**
     * Agent名称
     */
    private String name;


    /**
     * Agent版本
     */
    private String version;


    /**
     * Agent描述
     */
    private String description;

}
