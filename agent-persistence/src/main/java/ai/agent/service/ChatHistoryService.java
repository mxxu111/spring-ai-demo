package ai.agent.service;


import ai.agent.entity.ChatHistoryEntity;

import java.util.List;

/**
 * 聊天历史记录服务接口
 */
public interface ChatHistoryService {

    /**
     * 添加一条历史记录
     *
     * @param sessionId 会话ID
     * @param userName  用户名
     * @param role      角色(USER/ASSISTANT/SYSTEM)
     * @param content   消息内容
     */
    void addMessage(String sessionId, String userName, String role, String content);

    /**
     * 获取指定会话的历史记录
     *
     * @param sessionId 会话ID
     * @param limit     限制条数(null表示不限制)
     * @return 历史记录列表
     */
    List<ChatHistoryEntity> getHistory(String sessionId, Integer limit);

    /**
     * 获取用户的所有会话ID列表
     *
     * @param userName 用户名
     * @return 会话ID列表
     */
    List<String> getUserSessions(String userName);

    /**
     * 清空指定会话的历史记录
     *
     * @param sessionId 会话ID
     */
    void clearHistory(String sessionId);

    /**
     * 批量添加历史记录
     *
     * @param records 记录列表
     */
    void batchAddMessages(List<ChatHistoryEntity> records);
}