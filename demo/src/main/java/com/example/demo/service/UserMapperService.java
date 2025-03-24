package com.example.demo.service;

import com.example.demo.dtos.UserDTO;
import com.example.demo.model.UserModel;
import org.springframework.stereotype.Service;

@Service
public class UserMapperService {

    public UserDTO toDto(UserModel user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }
}
