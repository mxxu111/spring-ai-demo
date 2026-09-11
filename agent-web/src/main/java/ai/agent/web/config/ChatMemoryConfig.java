package ai.agent.web.config;


import ai.agent.service.ChatHistoryService;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatMemory 配置类
 *
 * 显式配置 PersistentChatMemory,确保覆盖 Spring AI 的默认 InMemoryChatMemory
 */
@Configuration
public class ChatMemoryConfig {

    /**
     * 注册持久化的 ChatMemory 实现
     *
     * Spring AI 默认会提供 InMemoryChatMemory (通过 @ConditionalOnMissingBean)
     * 这里我们显式定义 Bean,确保使用数据库持久化的实现
     *
     * @param chatHistoryService 聊天历史服务
     * @return PersistentChatMemory 实例
     */
    @Bean
    public ChatMemory chatMemory(ChatHistoryService chatHistoryService) {
        return new PersistentChatMemory(chatHistoryService);
    }
}
