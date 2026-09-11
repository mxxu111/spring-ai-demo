package ai.agent.web.controller;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 - 用于验证 ChatMemory 配置
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private ChatMemory chatMemory;

    /**
     * 测试 ChatMemory 实现类型
     */
    @GetMapping("/memory-type")
    public Map<String, Object> testMemoryType() {
        Map<String, Object> result = new HashMap<>();
        result.put("beanClass", chatMemory.getClass().getSimpleName());
        result.put("beanPackage", chatMemory.getClass().getPackage().getName());
        result.put("isPersistent", chatMemory.getClass().getSimpleName().equals("PersistentChatMemory"));
        result.put("message", "PersistentChatMemory".equals(chatMemory.getClass().getSimpleName())
            ? "✅ 正在使用数据库持久化的 ChatMemory"
            : "❌ 使用的是: " + chatMemory.getClass().getSimpleName());
        return result;
    }
}
