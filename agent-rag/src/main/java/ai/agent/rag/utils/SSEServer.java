package ai.agent.rag.utils;


import ai.enums.SSEMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SSEServer {

    // 存放所有用户的SseEmitter连接
    private static final Map<String, SseEmitter> sseClients = new ConcurrentHashMap<>();

    // 建立连接
    public static SseEmitter connect(String userId) {
        // 设置超时时间为0，即不超时，默认是30秒，超时未完成任务则会抛出异常
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 注册连接完成、超时、异常时的回调函数
        sseEmitter.onTimeout(timeoutCallback(userId));
        sseEmitter.onCompletion(completionCallback(userId));
        sseEmitter.onError(errorCallback(userId));

        sseClients.put(userId, sseEmitter);
        log.info("SSE connect, userId: {}", userId);
        return sseEmitter;
    }


    // 发送消息
    public static void sendMsg(String userId, String message, SSEMsgType msgType) {

        if (CollectionUtils.isEmpty(sseClients)) {
            return;
        }

        if (sseClients.containsKey(userId)) {
            SseEmitter sseEmitter = sseClients.get(userId);
            sendEmitterMessage(sseEmitter, userId, message, msgType);
        }
    }

    public static void sendMsgToAllUsers(String message) {
        if (CollectionUtils.isEmpty(sseClients)) {
            return;
        }

        sseClients.forEach((userId, sseEmitter) -> {
            sendEmitterMessage(sseEmitter, userId, message, SSEMsgType.MESSAGE);
        });
    }


    private static void sendEmitterMessage(SseEmitter sseEmitter,
                                           String userId,
                                           String message,
                                           SSEMsgType msgType) {
        // 指定事件名称(name)，前端根据这个名称监听
        SseEmitter.SseEventBuilder msgEvent = SseEmitter.event()
                .id(userId)
                .data(message)
                .name(msgType.type);

        try {
            sseEmitter.send(msgEvent);
        } catch (IOException e) {
            log.error("SSE send message error, userId: {}, error: {}", userId, e.getMessage());
            close(userId);  // 发送异常时，移除该连接
        }
    }


    // 关闭连接
    public static void close(String userId) {
        SseEmitter emitter = sseClients.get(userId);
        if (emitter != null) {
            emitter.complete(); // 这会触发 onCompletion 回调,回调中已经包含了 remove 操作
        }
    }

    // 超时回调
    private static Runnable timeoutCallback(String userId) {
        return () -> {
            log.warn("SSE connection timeout, userId: {}", userId);
            sseClients.remove(userId);
        };
    }

    // 完成回调
    private static Runnable completionCallback(String userId) {
        return () -> {
            log.info("SSE connection completed, userId: {}", userId);
            sseClients.remove(userId);
        };
    }

    // 异常回调
    private static Consumer<Throwable> errorCallback(String userId) {
        return throwable -> {
            log.error("SSE connection error, userId: {}, error: {}", userId, throwable.getMessage());
            sseClients.remove(userId);
        };
    }

}

