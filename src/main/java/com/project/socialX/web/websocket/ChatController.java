package com.project.socialX.web.websocket;

import com.project.socialX.service.ChatService;
import com.project.socialX.service.dto.Chat.MessageRequest;
import com.project.socialX.service.dto.Chat.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequest request, java.security.Principal principal) {
        log.info("WebSocket message received: {}", request);
        log.info("Principal from WebSocket Session: {}", principal != null ? principal.getName() : "NULL");
        
        // Copy Authentication từ WebSocket Session sang Thread Local của SecurityContext
        if (principal instanceof org.springframework.security.core.Authentication) {
            org.springframework.security.core.context.SecurityContextHolder.getContext()
                .setAuthentication((org.springframework.security.core.Authentication) principal);
            log.info("Successfully set SecurityContextHolder for user: {}", principal.getName());
        } else {
            log.warn("Principal is not an instance of Authentication or is NULL!");
        }
        
        try {
            chatService.sendMessage(request);
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            log.info("Cleared SecurityContextHolder");
        }
    }
}
