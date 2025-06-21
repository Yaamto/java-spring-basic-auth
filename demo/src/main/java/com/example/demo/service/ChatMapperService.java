package com.example.demo.service;

import com.example.demo.dtos.ChatMessageDTO;
import com.example.demo.dtos.UserDTO;
import com.example.demo.model.ChatMessageModel;
import com.example.demo.model.UserModel;
import org.springframework.stereotype.Service;

@Service
public class ChatMapperService {

    public ChatMessageDTO toDto(ChatMessageModel message) {
        UserModel sender = message.getSender();
        UserModel receiver = message.getReceiver();

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());

        if (sender != null) {
            dto.setSenderId(sender.getId());
            dto.setSenderUsername(sender.getUsername());
        }

        if (receiver != null) {
            dto.setReceiverId(receiver.getId());
            dto.setReceiverUsername(receiver.getUsername());
        }

        return dto;
    }
}
