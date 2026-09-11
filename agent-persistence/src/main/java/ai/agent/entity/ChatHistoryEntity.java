package ai.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天历史记录实体类
 */
@Data
@TableName("chat_history")
public class ChatHistoryEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户名/用户ID
     */
    private String userName;

    /**
     * 消息角色: USER/ASSISTANT/SYSTEM
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 消息类型: TEXT/IMAGE/etc
     */
    private String messageType;

    /**
     * 扩展字段(JSON格式,存储额外信息)
     */
    private String metadata;
}