package ai.agent.web.controller;


import ai.agent.entity.ChatHistoryEntity;
import ai.agent.service.ChatHistoryService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天历史记录控制器
 */
@RestController
@RequestMapping("/api/chat/history")
@Slf4j
public class ChatHistoryController {

    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * 获取指定会话的历史记录
     *
     * @param sessionId 会话ID
     * @param limit     限制条数(可选,默认50)
     * @return 历史记录列表
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<ChatHistoryEntity>> getSessionHistory(
            @PathVariable String sessionId,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        log.info("查询会话历史: sessionId={}, limit={}", sessionId, limit);
        List<ChatHistoryEntity> history = chatHistoryService.getHistory(sessionId, limit);
        return ResponseEntity.ok(history);
    }

    /**
     * 获取用户的所有会话列表
     *
     * @param userName 用户名
     * @return 会话ID列表
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<String>> getUserSessions(@RequestParam String userName) {
        log.info("查询用户会话列表: userName={}", userName);
        List<String> sessions = chatHistoryService.getUserSessions(userName);
        return ResponseEntity.ok(sessions);
    }

    /**
     * 清空指定会话的历史记录
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse> clearSessionHistory(@PathVariable String sessionId) {
        log.info("清空会话历史: sessionId={}", sessionId);
        try {
            chatHistoryService.clearHistory(sessionId);
            return ResponseEntity.ok(ApiResponse.success("会话历史已清空"));
        } catch (Exception e) {
            log.error("清空会话历史失败: sessionId={}", sessionId, e);
            return ResponseEntity.ok(ApiResponse.error("清空失败: " + e.getMessage()));
        }
    }

    /**
     * 统一响应格式
     */
    @Data
    public static class ApiResponse {
        private Integer code;
        private String message;

        public static ApiResponse success(String message) {
            ApiResponse response = new ApiResponse();
            response.setCode(200);
            response.setMessage(message);
            return response;
        }

        public static ApiResponse error(String message) {
            ApiResponse response = new ApiResponse();
            response.setCode(500);
            response.setMessage(message);
            return response;
        }
    }
}