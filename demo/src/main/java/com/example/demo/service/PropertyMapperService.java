package com.example.demo.service;

import com.example.demo.dtos.PropertyDTO;
import com.example.demo.dtos.PropertyImageDTO;
import com.example.demo.dtos.UserDTO;
import com.example.demo.model.PropertyModel;
import com.example.demo.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyMapperService {

    private final ImageStorageService imageStorageService;

    public PropertyDTO toDto(PropertyModel property) {

        UserModel owner = property.getOwner();
        UserDTO ownerDto = null;
        if (owner != null) {
            ownerDto = new UserDTO(
                    owner.getId(),
                    owner.getUsername(),
                    owner.getEmail()
            );
        }

        List<PropertyImageDTO> imagesDtos = property.getImages().stream()
                .map(image -> new PropertyImageDTO(
                        image.getId(),
                        image.getFileName(),
                        image.getContentType(),
                        imageStorageService.getBase64Data(image)
                ))
                .collect(Collectors.toList());

        return new PropertyDTO(
                property.getId(),
                property.getTitle(),
                property.getDescription(),
                property.getPrice(),
                property.getSurface(),
                property.getRooms(),
                property.getBathrooms(),
                property.getPropertyType(),
                property.getPropertyTransaction(),
                property.getCity(),
                property.getPostalCode(),
                property.getAddress(),
                property.getLatitude(),
                property.getLongitude(),
                property.isGarage(),
                property.isGarden(),
                property.isSwimmingPool(),
                property.isElevator(),
                property.isBalcony(),
                property.isMeubled(),
                property.isAirConditioned(),
                property.isFibred(),
                ownerDto,
                property.getCreatedAt(),
                imagesDtos
        );
    }
}