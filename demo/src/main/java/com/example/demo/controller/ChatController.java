package com.example.demo.controller;

import com.example.demo.dtos.ChatMessageDTO;
import com.example.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @PathVariable Long receiverId,
            @RequestBody ChatMessageDTO message) {
        return ResponseEntity.ok(chatService.sendMessage(receiverId, message.getContent()));
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getConversation(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getConversation(userId));
    }

    @GetMapping("/conversations/preview")
    public ResponseEntity<List<ChatMessageDTO>> getConversationsPreview() {
        return ResponseEntity.ok(chatService.getLastMessages());
    }

    @MessageMapping("/private-message")
    public void handlePrivateMessage(@Payload ChatMessageDTO message) {
        ChatMessageDTO savedMessage = chatService.sendMessage(message.getReceiverId(), message.getContent());

        // Envoie au destinataire spécifique
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiverId()),
                "/private",
                savedMessage
        );

        // Envoie une copie à l'expéditeur
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getSenderId()),
                "/private",
                savedMessage
        );
    }
}
