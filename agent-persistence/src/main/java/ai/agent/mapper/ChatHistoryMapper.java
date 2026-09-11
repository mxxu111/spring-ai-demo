package ai.agent.mapper;


import ai.agent.entity.ChatHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface ChatHistoryMapper
        extends BaseMapper<ChatHistoryEntity> {


    /**
     * 查询会话历史
     */
    @Select("""
        SELECT *
        FROM chat_history
        WHERE session_id = #{sessionId}
        ORDER BY create_time ASC
        LIMIT #{limit}
    """)
    List<ChatHistoryEntity> selectHistory(
            @Param("sessionId") String sessionId,
            @Param("limit") Integer limit
    );


    /**
     * 查询用户所有会话
     */
    @Select("""
        SELECT DISTINCT session_id
        FROM chat_history
        WHERE user_name = #{userName}
        ORDER BY MAX(create_time) DESC
    """)
    List<String> selectUserSessions(
            @Param("userName") String userName
    );


    /**
     * 删除会话
     */
    @Delete("""
        DELETE FROM chat_history
        WHERE session_id = #{sessionId}
    """)
    int deleteBySessionId(
            @Param("sessionId") String sessionId
    );


}