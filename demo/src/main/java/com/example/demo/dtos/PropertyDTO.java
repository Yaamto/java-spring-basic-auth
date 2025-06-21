package com.example.demo.dtos;

import com.example.demo.model.PropertyTransaction;
import com.example.demo.model.PropertyType;

import java.time.LocalDateTime;
import java.util.List;

public record PropertyDTO(
        Long id,
        String title,
        String description,
        Long price,
        Long surface,
        Long rooms,
        Long bathrooms,
        PropertyType propertyType,
        PropertyTransaction propertyTransaction,
        String city,
        Long postalCode,
        String address,
        Float latitude,
        Float longitude,
        boolean isGarage,
        boolean isGarden,
        boolean isSwimmingPool,
        boolean isElevator,
        boolean isBalcony,
        boolean isMeubled,
        boolean isAirConditioned,
        boolean isFibred,
        UserDTO owner,
        LocalDateTime createdAt,
        List<PropertyImageDTO> images
) {}
