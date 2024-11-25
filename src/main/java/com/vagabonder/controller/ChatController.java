package com.vagabonder.controller;

import com.vagabonder.dto.*;
import com.vagabonder.entity.User;
import com.vagabonder.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/check-status/{otherUserId}")
    public ResponseEntity<ConversationResponseDTO> checkChatStatus(@AuthenticationPrincipal User currentUser, @PathVariable UUID otherUserId) {
        ConversationResponseDTO response = chatService.initiateChat(currentUser.getId(), otherUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getUserConversations(@AuthenticationPrincipal User currentUser) {
        List<ConversationDTO> conversations = chatService.getUserConversations(currentUser.getId());
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageDTO>> getConversationMessages(@AuthenticationPrincipal User currentUser, @PathVariable UUID conversationId) {
        List<MessageDTO> messages = chatService.getConversationMessages(conversationId, currentUser.getId());
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/send-message")
    public ResponseEntity<MessageResponseDTO> sendMessage(@AuthenticationPrincipal User currentUser, @RequestBody SendMessageRequestDTO request) { //todo validate message
        MessageResponseDTO response = chatService.sendMessage(currentUser.getId(), request.getReceiverId(), request.getContent());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ChatRequestDTO>> getReceivedChatRequests(@AuthenticationPrincipal User currentUser) {
        List<ChatRequestDTO> requests = chatService.getReceivedChatRequests(currentUser.getId());
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/requests/{requestId}/handle")
    public ResponseEntity<ConversationDTO> handleChatRequest(@AuthenticationPrincipal User currentUser, @PathVariable UUID requestId, @RequestParam boolean isAccept) {
        ConversationDTO conversation = chatService.handleChatRequest(requestId, currentUser.getId(), isAccept);
        return ResponseEntity.ok(conversation);
    }


}