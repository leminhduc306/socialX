package com.project.socialX.web.rest.impl;

import com.project.socialX.dto.response.Response;
import com.project.socialX.service.ConversationService;
import com.project.socialX.service.dto.Chat.ConversationResponse;
import com.project.socialX.service.dto.Chat.CreateGroupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<Response<List<ConversationResponse>>> getMyConversations() {
        return ResponseEntity.ok(Response.ok(conversationService.getMyConversations()));
    }

    @PostMapping("/direct/{targetUserId}")
    public ResponseEntity<Response<ConversationResponse>> getOrCreateDirectConversation(
            @PathVariable Long targetUserId) {
        return ResponseEntity.ok(Response.ok(conversationService.getOrCreateDirectConversation(targetUserId)));
    }

    @PostMapping("/group")
    public ResponseEntity<Response<ConversationResponse>> createGroupConversation(
            @RequestBody CreateGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.created(conversationService.createGroupConversation(request)));
    }

    @PutMapping("/group/{conversationId}")
    public ResponseEntity<Response<ConversationResponse>> updateGroupInfo(
            @PathVariable Long conversationId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile avatar) {
        return ResponseEntity.ok(Response.ok(conversationService.updateGroupInfo(conversationId, name, avatar)));
    }

    @PostMapping("/{conversationId}/members/{userId}")
    public ResponseEntity<Response<ConversationResponse>> addMember(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(Response.ok(conversationService.addMember(conversationId, userId)));
    }

    @DeleteMapping("/{conversationId}/members/{userId}")
    public ResponseEntity<Response<Void>> removeMember(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        conversationService.removeMember(conversationId, userId);
        return ResponseEntity.ok(Response.ok(null));
    }
}
