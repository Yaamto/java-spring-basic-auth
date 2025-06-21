package com.example.demo.dtos;

import com.example.demo.model.PropertyTransaction;
import com.example.demo.model.PropertyType;

import java.time.LocalDateTime;

public interface PropertySearchDTO {
    Long getId();
    String getTitle();
    String getDescription();
    Double getPrice();
    Double getSurface();
    Integer getRooms();
    Integer getBathrooms();
    PropertyType getPropertyType();
    PropertyTransaction getPropertyTransaction();
    String getCity();
    String getPostalCode();
    String getAddress();
    Double getLatitude();
    Double getLongitude();
    LocalDateTime getCreatedAt();
    String getFirstImageName();
}
