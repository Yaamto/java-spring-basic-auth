package com.example.demo.service;

import com.example.demo.dtos.ChatMessageDTO;
import com.example.demo.exception.GenericException;
import com.example.demo.model.ChatMessageModel;
import com.example.demo.model.UserModel;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatMapperService chatMapperService;

    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository,
                      UserRepository userRepository,
                      ChatMapperService chatMapperService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.chatMapperService = chatMapperService;
    }

    public ChatMessageDTO sendMessage(Long receiverId, String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel sender = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new GenericException("Sender not found"));
        UserModel receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new GenericException("Receiver not found"));

        ChatMessageModel message = new ChatMessageModel();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        ChatMessageModel savedMessage = chatMessageRepository.save(message);
        return chatMapperService.toDto(savedMessage);
    }

    public List<ChatMessageDTO> getConversation(Long otherUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new GenericException("Current user not found"));
        UserModel otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new GenericException("User not found"));

        return chatMessageRepository.findConversation(Optional.ofNullable(currentUser), otherUser)
                .stream()
                .map(chatMapperService::toDto)
                .collect(Collectors.toList());
    }

    public List<ChatMessageDTO> getLastMessages() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new GenericException("User not found"));

        return chatMessageRepository.findLastMessagesForUser(currentUser.getId())
                .stream()
                .map(chatMapperService::toDto)
                .collect(Collectors.toList());
    }
}
