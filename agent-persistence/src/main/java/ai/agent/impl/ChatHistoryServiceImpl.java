package ai.agent.service.impl;


import ai.agent.entity.ChatHistoryEntity;
import ai.agent.mapper.ChatHistoryMapper;
import ai.agent.service.ChatHistoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements ChatHistoryService {


    private final ChatHistoryMapper chatHistoryMapper;



    /**
     * 添加单条消息
     */
    @Override
    public void addMessage(String sessionId, String userName, String role, String content) {
        ChatHistoryEntity entity = new ChatHistoryEntity();
        entity.setSessionId(sessionId);
        entity.setUserName(userName);
        entity.setRole(role);
        entity.setContent(content);
        entity.setCreateTime(LocalDateTime.now());
        chatHistoryMapper.insert(entity);
    }



    /**
     * 查询历史消息
     */
    @Override
    public List<ChatHistoryEntity> getHistory(String sessionId, Integer limit) {
        if(limit == null){
            limit = 20;
        }
        return chatHistoryMapper.selectHistory(sessionId, limit);
    }

    /**
     * 查询用户会话列表
     */
    @Override
    public List<String> getUserSessions(String userName) {
        return chatHistoryMapper.selectUserSessions(userName);
    }

    /**
     * 清空会话
     */
    @Override
    @Transactional
    public void clearHistory(String sessionId) {
        chatHistoryMapper.deleteBySessionId(sessionId);
    }

    /**
     * 批量保存
     */
    @Override
    @Transactional
    public void batchAddMessages(List<ChatHistoryEntity> records) {
        if(records == null || records.isEmpty()){
            return;
        }
        records.forEach(item -> {
            if(item.getCreateTime()==null){
                item.setCreateTime(LocalDateTime.now());
            }
        });
        records.forEach(chatHistoryMapper::insert);
    }

}