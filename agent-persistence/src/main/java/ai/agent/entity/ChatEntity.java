package ai.agent.entity;

import ai.enums.ChatMode;
import lombok.Data;

/**
 * 聊天请求实体类
 */
@Data
public class ChatEntity {

    /**
     * 当前用户名/用户ID
     */
    private String currentUserName;

    /**
     * 用户发送的消息内容
     */
    private String message;

    /**
     * 会话ID（可选）
     */
    private String sessionId;

    /**
     * 是否使用知识库（RAG增强）
     */
    private ChatMode mode;
}