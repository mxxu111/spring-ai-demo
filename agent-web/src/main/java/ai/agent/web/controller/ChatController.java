package ai.agent.web.controller;


import ai.agent.entity.ChatEntity;
import ai.agent.model.AgentResult;
import ai.agent.model.Task;
import ai.agent.web.service.ChatService;
import ai.service.AgentService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;

    @Autowired
    private ChatService chatService;


    private final AgentService agentService;


    public ChatController(ChatClient.Builder chatClientBuilder, AgentService agentService) {
        this.chatClient = chatClientBuilder.build();
        this.agentService = agentService;
    }

    @GetMapping("/test")
    public String completion(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @PostMapping("/ai")
    public void chat(@RequestBody ChatEntity chatEntity) {
        chatService.doChat(chatEntity);
    }

    @PostMapping("/send")
    public void send(@RequestBody ChatEntity chatEntity) {
        chatService.doChat(chatEntity);
    }

    @PostMapping("/execute")
    public AgentResult execute(@RequestBody Task task) {

        return agentService.execute(task);
    }

}
