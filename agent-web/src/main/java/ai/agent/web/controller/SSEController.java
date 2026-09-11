package ai.agent.web.controller;

import ai.agent.rag.utils.SSEServer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
public class SSEController {

    @GetMapping(path = "/connect", produces = { MediaType.TEXT_EVENT_STREAM_VALUE })
    public SseEmitter connect(@RequestParam String userId) {
        return SSEServer.connect(userId);
    }

}

