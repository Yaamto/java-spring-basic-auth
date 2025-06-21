package com.example.demo.service;

import com.example.demo.dtos.PropertyDTO;
import com.example.demo.exception.GenericException;
import com.example.demo.model.*;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyMapperService propertyMapperService;

    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository, PropertyMapperService propertyMapperService) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.propertyMapperService = propertyMapperService;
    }

    public PropertyDTO saveProperty(PropertyModel property) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new GenericException("User not found"));
        property.setOwner(user);
        return propertyMapperService.toDto(propertyRepository.save(property));
    }

    public List<PropertyDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(propertyMapperService::toDto)
                .toList();
    }

    public List<PropertyDTO> getLastProperties() {
        Pageable topSix = PageRequest.of(0, 6);
        return propertyRepository.findAllByOrderByCreatedAtDesc(topSix).stream()
                .map(propertyMapperService::toDto)
                .toList();
    }

    public List<PropertyDTO> searchProperties(
            PropertyType propertyType,
            PropertyTransaction propertyTransaction,
            Long minSurface,
            Long maxPrice,
            Long minRooms,
            String city) {

        return propertyRepository.searchProperties(
                propertyType,
                propertyTransaction,
                minSurface,
                maxPrice,
                minRooms,
                city
        ).stream()
                .map(property -> {
                    if (!property.getImages().isEmpty()) {
                        property.setImages(List.of(property.getImages().get(0)));
                    }
                    return propertyMapperService.toDto(property);
                })
                .toList();
    }

    public PropertyDTO getPropertyById(Long id) {
        return propertyMapperService.toDto(
                propertyRepository.findById(id)
                        .orElseThrow(() -> new GenericException("Property not found"))
        );
    }

    public List<PropertyDTO> getMyProperties() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new GenericException("User not found"));
        return propertyRepository.findByOwner(user).stream()
                .map(propertyMapperService::toDto)
                .toList();
    }
}
