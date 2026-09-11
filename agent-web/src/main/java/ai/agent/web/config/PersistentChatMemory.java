package ai.agent.web.config;

import ai.agent.entity.ChatHistoryEntity;
import ai.agent.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于MySQL持久化的ChatMemory实现
 * 实现Spring AI的ChatMemory接口,将对话历史保存到数据库
 *
 * 注意: 不使用 @Component,通过 ChatMemoryConfig 配置类创建
 */
@Slf4j
public class PersistentChatMemory implements ChatMemory {

    private final ChatHistoryService chatHistoryService;

    /**
     * 内存缓存,用于减少数据库查询
     * Key: sessionId, Value: 最近的消息列表
     */
    private final Map<String, List<Message>> memoryCache = new ConcurrentHashMap<>();

    /**
     * 每个会话保留的最大消息数量
     */
    private static final int MAX_HISTORY_SIZE = 50;

    /**
     * 构造函数
     *
     * @param chatHistoryService 聊天历史服务
     */
    public PersistentChatMemory(ChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;
        log.info("PersistentChatMemory 初始化完成,使用数据库持久化");
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        String userName = extractUserName(conversationId);

        // 保存到数据库
        for (Message message : messages) {
            String role = getMessageRole(message);
            String content = message.getText();

            if (content != null && !content.isEmpty()) {
                chatHistoryService.addMessage(conversationId, userName, role, content);
            }
        }

        // 更新内存缓存
        List<Message> currentHistory = memoryCache.getOrDefault(conversationId, List.of());
        List<Message> newHistory = concat(currentHistory, messages);
        // 限制历史记录数量
        if (newHistory.size() > MAX_HISTORY_SIZE) {
            newHistory = newHistory.subList(newHistory.size() - MAX_HISTORY_SIZE, newHistory.size());
        }
        memoryCache.put(conversationId, newHistory);

        log.debug("添加对话记忆: conversationId={}, messageCount={}", conversationId, messages.size());
    }

    @Override
    public List<Message> get(String conversationId) {
        // 先从内存缓存获取
        List<Message> cachedMessages = memoryCache.get(conversationId);

        if (cachedMessages == null) {
            // 缓存不存在,从数据库加载
            cachedMessages = loadHistoryFromDatabase(conversationId);
            memoryCache.put(conversationId, cachedMessages);
        }

        return cachedMessages;
    }

    @Override
    public void clear(String conversationId) {
        // 清除内存缓存
        memoryCache.remove(conversationId);

        // 清除数据库记录
        chatHistoryService.clearHistory(conversationId);

        log.info("清空对话记忆: conversationId={}", conversationId);
    }

    /**
     * 从数据库加载历史记录
     */
    private List<Message> loadHistoryFromDatabase(String conversationId) {
        List<ChatHistoryEntity> historyList =
                chatHistoryService.getHistory(conversationId, MAX_HISTORY_SIZE);

        return historyList.stream()
                .map(this::convertToMessage)
                .collect(Collectors.toList());
    }

    /**
     * 将数据库实体转换为Message对象
     */
    private Message convertToMessage(ChatHistoryEntity entity) {
        String role = entity.getRole();
        String content = entity.getContent();

        if ("USER".equalsIgnoreCase(role)) {
            return new UserMessage(content);
        } else if ("ASSISTANT".equalsIgnoreCase(role)) {
            return new AssistantMessage(content);
        } else {
            // 默认返回UserMessage
            return new UserMessage(content);
        }
    }

    /**
     * 获取消息的角色
     */
    private String getMessageRole(Message message) {
        MessageType messageType = message.getMessageType();
        if (messageType == MessageType.USER) {
            return "USER";
        } else if (messageType == MessageType.ASSISTANT) {
            return "ASSISTANT";
        } else if (messageType == MessageType.SYSTEM) {
            return "SYSTEM";
        }
        return "USER";
    }

    /**
     * 从conversationId中提取用户名
     * 假设conversationId格式为 "userName" 或 "userName_sessionId"
     */
    private String extractUserName(String conversationId) {
        if (conversationId == null) {
            return "unknown";
        }
        // 简单处理:直接返回conversationId作为userName
        // 实际项目中可以根据需要调整
        return conversationId.split("_")[0];
    }

    /**
     * 合并消息列表
     */
    private List<Message> concat(List<Message> list1, List<Message> list2) {
        return java.util.stream.Stream.concat(
                list1.stream(),
                list2.stream()
        ).collect(Collectors.toList());
    }
}