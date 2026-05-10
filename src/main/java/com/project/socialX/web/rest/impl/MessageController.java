package com.project.socialX.web.rest.impl;

import com.project.socialX.domain.enums.MessageType;
import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.dto.response.Response;
import com.project.socialX.service.ChatService;
import com.project.socialX.service.dto.Chat.MessageRequest;
import com.project.socialX.service.dto.Chat.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final ChatService chatService;

    @PostMapping(value = "/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<MessageResponse>> sendMediaMessage(
            @RequestParam(required = false) Long conversationId,
            @RequestParam(required = false) Long recipientId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "IMAGE") MessageType type) {
        return ResponseEntity.ok(Response.ok(chatService.sendMediaMessage(conversationId, recipientId, file, type)));
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Response<PagingResponse<MessageResponse>>> getMessages(
            @PathVariable Long conversationId,
            @ModelAttribute PagingRequest pagingRequest) {
        return ResponseEntity.ok(Response.ok(chatService.getMessages(conversationId, pagingRequest)));
    }
}
