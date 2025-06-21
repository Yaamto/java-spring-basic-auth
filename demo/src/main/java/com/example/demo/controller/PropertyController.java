package com.example.demo.controller;

import com.example.demo.dtos.PropertyDTO;
import com.example.demo.model.*;
import com.example.demo.service.ImageStorageService;
import com.example.demo.service.PropertyMapperService;
import com.example.demo.service.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/property")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;
    private final PropertyMapperService propertyMapperService;
    private final ImageStorageService imageStorageService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PropertyDTO> addProperty(
            @RequestPart("property") String propertyJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        PropertyModel property = objectMapper.readValue(propertyJson, PropertyModel.class);

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                PropertyImage propertyImage = imageStorageService.createPropertyImage(image);
                propertyImage.setProperty(property);
                property.getImages().add(propertyImage);
            }
        }

        return ResponseEntity.ok(propertyService.saveProperty(property));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PropertyDTO>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PropertyDTO>> getRecentProperties() {
        return ResponseEntity.ok(propertyService.getLastProperties());
    }

    @GetMapping("/search")
    public List<PropertyDTO> searchProperties(
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) PropertyTransaction propertyTransaction,
            @RequestParam(required = false) Long minSurface,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Long minRooms,
            @RequestParam(required = false) String city
    ) {
        return propertyService.searchProperties(propertyType, propertyTransaction, minSurface, maxPrice, minRooms, city);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
            return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @GetMapping("/myProperties")
    public ResponseEntity<List<PropertyDTO>> getMyProperties() {
        return ResponseEntity.ok(propertyService.getMyProperties());
    }
}
