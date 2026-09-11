# MCP 桥接层实现说明

## 一、架构演进

### 1.1 旧架构（基于 HTTP）

```
Spring AI ChatClient
        ↓ (HTTP)
GatewayToolCallbackProvider (agent-web)
        ↓ (HTTP POST)
mcp-gateway-service (Gateway)
        ↓ (HTTP/JSON-RPC)
mcp-server
```

**问题**：
- 双重 HTTP 开销（agent-web → Gateway → MCP Server）
- GatewayToolCallback.getToolDefinition() 返回 null（未完整实现）
- 架构复杂，调试困难

### 1.2 新架构（直接桥接）

```
Spring AI ChatClient
        ↓
MCPToolCallbackProvider (bridge layer)
        ↓ (直接内部调用)
ToolInvokeService / ToolRouter
        ↓
MCPConnection (Spring AI MCP Client)
        ↓ (SSE/JSON-RPC)
mcp-server
```

**优势**：
- 无 HTTP 开销，直接内部调用
- 完整实现 ToolDefinition
- 架构简洁，易于理解和维护
- 支持动态刷新工具列表

---

## 二、核心组件

### 2.1 MCPToolCallback（实现 ToolCallback）

**文件位置**：`mcp-gateway-service/src/main/java/ai/agent/gateway/bridge/MCPToolCallback.java`

**职责**：
- 实现 Spring AI 的 `ToolCallback` 接口
- 封装 MCP 工具的元数据（名称、描述、参数 Schema）
- 提供工具调用入口，通过构造函数注入调用逻辑

**关键代码**：
```java
public class MCPToolCallback implements ToolCallback {
    private final ToolDefinition toolDefinition;
    private final ToolInvoker toolInvoker;

    @Override
    public ToolDefinition getToolDefinition() {
        return toolDefinition; // 完整实现，不再返回 null
    }

    @Override
    public String call(String functionInput) {
        Map<String, Object> arguments = parseArguments(functionInput);
        Object result = toolInvoker.invoke(toolDefinition.name(), arguments);
        return serializeResult(result);
    }
}
```

### 2.2 MCPToolCallbackProvider（实现 ToolCallbackProvider）

**文件位置**：`mcp-gateway-service/src/main/java/ai/agent/gateway/bridge/MCPToolCallbackProvider.java`

**职责**：
- 实现 Spring AI 的 `ToolCallbackProvider` 接口
- 从 MCPToolRegistry 获取所有 MCP 工具
- 将每个工具包装成 MCPToolCallback
- 支持动态刷新工具列表

**关键代码**：
```java
@Component
public class MCPToolCallbackProvider implements ToolCallbackProvider {
    private final MCPToolRegistry toolRegistry;

    @Override
    public ToolCallback[] getToolCallbacks() {
        return toolRegistry.getAllTools().stream()
            .map(tool -> new MCPToolCallback(tool, toolInvoker, objectMapper))
            .toArray(ToolCallback[]::new);
    }
}
```

### 2.3 BridgeAutoConfiguration（自动配置）

**文件位置**：`mcp-gateway-service/src/main/java/ai/agent/gateway/bridge/BridgeAutoConfiguration.java`

**职责**：
- 将 ToolInvokeService 注入到 MCPToolCallbackProvider
- 配置同步/异步调用适配器
- 处理 Reactive 到同步的转换

**关键代码**：
```java
@Configuration
public class BridgeAutoConfiguration {
    @PostConstruct
    public void configureToolInvoker() {
        MCPToolCallback.ToolInvoker invoker = (toolName, arguments) -> {
            ToolRequest request = ToolRequest.builder()
                .toolName(toolName)
                .arguments(arguments)
                .build();

            // 异步转同步
            ToolResponse response = toolInvokeService.invoke(request).block();
            return response.getData();
        };

        toolCallbackProvider.setToolInvoker(invoker);
    }
}
```

---

## 三、使用方式

### 3.1 在 ChatClient 中使用

```java
@Service
public class ChatService {

    @Autowired
    private ToolCallbackProvider toolCallbackProvider; // 自动注入 MCPToolCallbackProvider

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    public String chat(String userMessage) {
        ChatClient chatClient = chatClientBuilder
            .defaultTools(toolCallbackProvider) // 注册所有 MCP 工具
            .build();

        return chatClient.prompt()
            .user(userMessage)
            .call()
            .content();
    }
}
```

### 3.2 在 Agent 中使用

```java
@Service
public class AgentService {

    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    public void executeWithTools() {
        Agent agent = Agent.builder()
            .tools(toolCallbackProvider.getToolCallbacks())
            .build();

        agent.execute("Send an email to test@example.com");
    }
}
```

---

## 四、测试验证

### 4.1 单元测试

**MCPToolCallbackTest.java**
```java
@Test
void testCallWithValidInput() {
    MCPToolCallback callback = new MCPToolCallback(testTool, toolInvoker, objectMapper);

    String result = callback.call("{\"location\": \"Beijing\"}");

    assertNotNull(result);
    assertTrue(result.contains("25°C"));
}
```

**MCPToolCallbackProviderTest.java**
```java
@Test
void testGetToolCallbacks() {
    when(toolRegistry.getAllTools()).thenReturn(Arrays.asList(tool1, tool2));

    ToolCallback[] callbacks = provider.getToolCallbacks();

    assertEquals(2, callbacks.length);
    assertEquals("get_weather", callbacks[0].getToolDefinition().name());
}
```

### 4.2 集成测试

**启动顺序**：
1. 启动 mcp-server (端口 8080)
2. 启动 mcp-gateway-service (端口 8082)
3. 验证工具加载：`curl http://localhost:8082/api/mcp/tools`
4. 在 ChatClient 中测试工具调用

**验证点**：
- ✅ MCPToolCallbackProvider 自动注入
- ✅ getToolCallbacks() 返回正确的工具列表
- ✅ ToolDefinition 完整实现
- ✅ 工具调用成功返回结果

---

## 五、与 HTTP 方式对比

| 维度 | HTTP 方式（旧） | 桥接层方式（新） |
|------|----------------|------------------|
| 性能 | 双重 HTTP 开销 | 无 HTTP 开销 |
| 架构 | 复杂（三层） | 简洁（直接桥接） |
| ToolDefinition | 未实现（返回 null） | 完整实现 |
| 调试 | 困难（跨进程） | 简单（同进程） |
| 维护性 | 低（多个组件） | 高（集中管理） |

**建议**：新架构适用于 mcp-gateway-service 作为 Spring AI 应用的场景。如果需要独立的 Gateway 服务供多个应用调用，可继续使用 HTTP 方式。

---

## 六、后续优化

### 6.1 性能优化

- [ ] 添加工具调用结果缓存
- [ ] 优化 Reactive 到同步转换（考虑虚拟线程）
- [ ] 支持异步工具调用（如果 Spring AI 支持）

### 6.2 功能增强

- [ ] 支持工具调用超时配置
- [ ] 添加工具调用重试机制
- [ ] 支持工具调用链追踪

### 6.3 可观测性

- [ ] 添加 Micrometer 指标
- [ ] 集成 OpenTelemetry 追踪
- [ ] 完善日志记录

---

## 七、已完成的文件修改

1. **mcp-gateway-service/src/main/java/ai/agent/gateway/client/MCPConnection.java**
   - 实现了 connect() - 建立 HTTP 连接到 MCP Server
   - 实现了 listTools() - 返回缓存的工具列表
   - 实现了 invokeTool() - 通过 HTTP POST 调用工具
   - 添加了工具缓存机制

2. **agent-web/src/main/java/ai/agent/web/tool/GatewayToolCallbackProvider.java** (新建)
   - 从 Gateway 的 /api/mcp/tools 端点获取工具列表
   - 将工具信息转换为 ToolCallback
   - 提供工具刷新功能

3. **agent-web/src/main/java/ai/agent/web/tool/GatewayToolCallback.java** (新建)
   - 实现 ToolCallback 接口
   - 通过 HTTP POST 调用 Gateway 的工具执行端点
   - 序列化参数和反序列化结果

4. **agent-web/src/main/java/ai/agent/web/impl/ChatServiceImpl.java**
   - 集成 GatewayToolCallbackProvider
   - 使用 Gateway 提供的工具

5. **mcp-gateway-service/src/main/resources/application.yml**
   - 端口改为 8082（避免与 mcp-server 冲突）
   - 配置连接到 mcp-server (http://localhost:8080)

6. **agent-web/src/main/resources/application.yml**
   - 添加 Gateway URL 配置（http://localhost:8082）
   - 禁用直接的 MCP Client

## 二、架构说明

### 调用链路

```
用户 → agent-web (ChatClient) → GatewayToolCallback
  → HTTP POST → mcp-gateway-service (Gateway)
  → ToolRouter → MCPConnection.invokeTool()
  → HTTP POST → mcp-server (MCP Server)
  → Spring AI MCP Framework → EmailTool.sendEmail()
```

### 关键组件

1. **mcp-server** (端口 8080)
   - 提供 EmailTool、DateTool、FileTool
   - 通过 SSE endpoint /sse 暴露 MCP 服务

2. **mcp-gateway-service** (端口 8082)
   - 管理 MCP Server 连接
   - 提供工具发现和调用 REST API
   - 路由工具调用到正确的 MCP Server

3. **agent-web** (端口 8081)
   - ChatClient 通过 GatewayToolCallbackProvider 获取工具
   - GatewayToolCallback 调用 Gateway 的 REST API

## 三、启动和测试步骤

### 3.1 启动顺序

**步骤 1：启动 mcp-server**
```bash
cd D:/workspace/mcp/mcp-server
mvn spring-boot:run
```

验证：
- 访问 http://localhost:8080/sse 应建立 SSE 连接
- 查看日志确认 EmailTool 已注册

**步骤 2：启动 mcp-gateway-service**
```bash
cd D:/workspace/mcp/mcp-gateway-service
mvn spring-boot:run
```

验证：
- 查看日志确认连接到 mcp-server
- 访问 http://localhost:8082/api/mcp/tools 应返回工具列表

**步骤 3：启动 agent-web**
```bash
cd D:/workspace/mcp/agent-web
mvn spring-boot:run
```

验证：
- 查看日志确认 ChatServiceImpl 已初始化
- 查看 GatewayToolCallbackProvider 已加载工具

### 3.2 功能测试

**测试 1：验证 Gateway 工具列表**

```bash
curl http://localhost:8082/api/mcp/tools
```

预期返回：
```json
[
  {
    "name": "sendEmail",
    "description": "给指定邮箱发送邮件信息。",
    "serverName": "mcp-server"
  }
]
```

**测试 2：直接通过 Gateway 调用工具**

```bash
curl -X POST http://localhost:8082/api/mcp/tools/sendEmail/invoke \
  -H "Content-Type: application/json" \
  -d '{
    "toolName": "sendEmail",
    "arguments": {
      "email": "test@example.com",
      "subject": "Test Email",
      "message": "Hello from MCP!",
      "contentType": 2
    },
    "caller": "test-user"
  }'
```

预期返回：
```json
{
  "success": true,
  "data": "邮件发送成功",
  "timestamp": "2026-08-06T..."
}
```

**测试 3：通过 ChatClient 测试完整链路**

1. 打开浏览器访问 http://localhost:8081
2. 发送消息："请给 test@example.com 发送一封测试邮件，主题是测试，内容是 Hello World"
3. 观察：
   - ChatClient 应识别需要调用 sendEmail 工具
   - GatewayToolCallback 应调用 Gateway
   - Gateway 应路由到 mcp-server
   - EmailTool 应执行发送邮件

### 3.3 调试技巧

1. **启用 DEBUG 日志**

在 application.yml 中设置：
```yaml
logging:
  level:
    ai.agent.gateway: DEBUG
    org.springframework.ai.mcp: DEBUG
```

2. **查看工具调用日志**

- MCPConnection.invokeTool() 会打印调用参数
- GatewayToolCallback.call() 会打印请求和响应
- EmailTool.sendEmail() 会打印邮件发送详情

3. **检查 SSE 连接**

```bash
curl -N http://localhost:8080/sse
```

应看到 SSE 事件流

## 四、已知问题和限制

### 4.1 当前实现的限制

1. **MCP 协议未完全实现**
   - 当前使用简单的 HTTP POST 而非标准的 MCP SSE 协议
   - 工具列表通过 REST API 而非 MCP tools/list 方法获取
   - 需要根据实际的 Spring AI MCP Client API 调整实现

2. **工具发现机制**
   - Gateway 需要手动配置连接到哪些 MCP Server
   - 缺少自动发现机制

3. **错误处理**
   - 需要更完善的错误处理和重试机制
   - 缺少连接断开后的自动重连

### 4.2 后续改进建议

1. **使用 Spring AI MCP Client 的正确 API**
   - 需要查阅 Spring AI 1.0.0 的 MCP Client 文档
   - 使用 McpClient 或 McpSyncClient 正确实现

2. **添加 SSE 心跳检测**
   - 定期检查连接状态
   - 自动重连机制

3. **完善工具缓存**
   - 定期刷新工具列表
   - 处理工具更新和删除

4. **安全性增强**
   - Gateway 添加认证机制
   - 工具调用权限控制

## 五、配置检查清单

### 5.1 mcp-server 配置

- [ ] 端口：8080
- [ ] SSE endpoint: /sse
- [ ] EmailTool 已配置 JavaMailSender
- [ ] 邮件服务器配置正确（smtp.163.com）

### 5.2 mcp-gateway-service 配置

- [ ] 端口：8082
- [ ] spring.ai.mcp.client.sse.connections.mcp-server.url: http://localhost:8080
- [ ] mcp.gateway.servers.mcp-server.name: mcp-server
- [ ] mcp.gateway.servers.mcp-server.url: http://localhost:8080

### 5.3 agent-web 配置

- [ ] 端口：8081
- [ ] mcp.gateway.url: http://localhost:8082
- [ ] spring.ai.mcp.client.enabled: false
- [ ] Ollama 配置正确（qwen3.5 模型）

## 六、成功标准

测试成功的标志：

1. 三个应用都能正常启动
2. Gateway 能列出 sendEmail 工具
3. 能通过 Gateway 直接调用 sendEmail 工具
4. ChatClient 能识别邮件发送请求并调用工具
5. 邮件成功发送到指定邮箱

---

**注意事项：**

- 确保 MySQL 数据库可访问（agent-web 需要）
- 确保 Milvus 向量数据库运行（如果使用 RAG 功能）
- 确保 Ollama 运行并有 qwen3.5 模型
- 邮件发送需要正确的邮箱授权码（不是登录密码）