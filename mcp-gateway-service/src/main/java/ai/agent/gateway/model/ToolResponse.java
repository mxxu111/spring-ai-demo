package ai.agent.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 工具调用响应模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolResponse {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 返回数据
     */
    private Object data;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 执行耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 响应时间
     */
    private LocalDateTime timestamp;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 成功响应
     */
    public static ToolResponse success(Object data) {
        return ToolResponse.builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 成功响应（带耗时）
     */
    public static ToolResponse success(Object data, Long durationMs) {
        return ToolResponse.builder()
                .success(true)
                .data(data)
                .durationMs(durationMs)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 失败响应
     */
    public static ToolResponse error(String errorMessage) {
        return ToolResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }

}