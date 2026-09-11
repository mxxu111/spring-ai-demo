package ai.agent.web.impl;


import ai.agent.entity.ChatEntity;
import ai.agent.rag.document.DocumentService;
import ai.agent.web.dto.SearchResult;
import ai.agent.web.service.ChatService;
import ai.agent.web.service.SearXngService;
import ai.agent.rag.utils.SSEServer;
import ai.enums.ChatMode;
import ai.enums.SSEMsgType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static ai.enums.ChatMode.*;


@Service
@Slf4j
public class ChatServiceImpl implements ChatService {


    private final ChatClient chatClient;


    @Resource
    private DocumentService documentService;


    @Resource
    private SearXngService searXngService;


    /**
     * 构造函数
     *
     * <p>使用 MCPToolCallbackProvider 提供的工具
     * <p>工具由 mcp-gateway-service 通过桥接层提供
     */
    public ChatServiceImpl(ChatClient.Builder chatClientBuilder, @Autowired(required = false) ToolCallbackProvider toolCallbackProvider, ChatMemory chatMemory) {

        // 使用 Gateway 提供的工具（如果可用）
        ChatClient.Builder builder = chatClientBuilder;

        if (toolCallbackProvider != null) {
            builder = builder.defaultToolCallbacks(toolCallbackProvider);
            log.info("ChatServiceImpl initialized with ToolCallbackProvider: {}",
                    toolCallbackProvider.getClass().getSimpleName());
        } else {
            log.warn("No ToolCallbackProvider available, ChatClient will not have tools");
        }

        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();

        log.info("ChatServiceImpl initialized successfully");
    }


    @Override
    public void doChat(ChatEntity chatEntity) {


        String userId = chatEntity.getCurrentUserName();
        String question = chatEntity.getMessage();
        Prompt prompt = buildPrompt(chatEntity.getMode(), question);
        log.info("开始AI对话 userId={}, mode={}", userId, chatEntity.getMode());
        Flux<String> stream =
                chatClient.prompt(prompt)
                        // 绑定会话ID
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                        .stream()
                        .content();
        stream.doOnError(e -> {
            log.error("AI流异常 userId={}", userId, e);
            SSEServer.sendMsg(userId, "服务异常，请稍后再试", SSEMsgType.FINISH);
            SSEServer.close(userId);
        }).subscribe(
                // 流式输出
                content -> {SSEServer.sendMsg(userId, content, SSEMsgType.ADD);},
                error -> {log.error("订阅异常 userId={}", userId, error);
                },
                () -> {
                    log.info("AI流结束 userId={}", userId);
                    SSEServer.sendMsg(userId, "done", SSEMsgType.FINISH);
                    SSEServer.close(userId);
                }
        );

    }


    /**
     * 根据模式构建Prompt
     */
    private Prompt buildPrompt(ChatMode mode, String question) {


        // =========================
        // 知识库 RAG
        // =========================

        if (KNOWLEDGE_BASE == mode) {

            List<org.springframework.ai.document.Document> docs = documentService.doSearch(question);
            String context = "没有找到相关知识";
            if (docs != null && !docs.isEmpty()) {
                context = docs.stream()
                        .map(org.springframework.ai.document.Document::getText)
                        .collect(Collectors.joining("\n---\n"));
            }
            String systemPrompt = """

                    你是企业知识库智能助手。

                    请根据下面知识库内容回答问题。

                    规则：
                    1. 优先使用知识库信息。
                    2. 不要提及"根据知识库"等内部信息。
                    3. 如果没有答案，请明确说明无法回答。


                    【知识库内容】

                    %s

                    """.formatted(context);
            return new Prompt(List.of(new SystemMessage(systemPrompt), new UserMessage(question)));
        }


        // =========================
        // 普通聊天
        // =========================

        if (DIRECT == mode) {
            return new Prompt(new UserMessage(question));
        }

        // =========================
        // 联网搜索
        // =========================

        if (INTERNET_SEARCH == mode) {
            List<SearchResult> results = searXngService.search(question);
            String context = "没有搜索结果";
            if (results != null && !results.isEmpty()) {
                context = results.stream()
                        .map(r ->
                                """
                                        标题:%s
                                        内容:%s
                                        链接:%s
                                        """.formatted(
                                        r.getTitle(),
                                        r.getContent(),
                                        r.getUrl()
                                )
                        )
                        .collect(Collectors.joining("\n---\n"));
            }

            String systemPrompt = """

                    你是联网搜索助手。

                    请根据搜索结果回答问题。


                    规则：

                    1. 使用搜索结果中的信息。
                    2. 不要说明自己查看了搜索结果。
                    3. 如果信息不足，直接说明。


                    【搜索结果】

                    %s

                    """.formatted(context);
            return new Prompt(List.of(new SystemMessage(systemPrompt), new UserMessage(question)));
        }
        throw new RuntimeException("未知聊天模式:" + mode);

    }

}