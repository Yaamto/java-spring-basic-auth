package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyImageDTO {
    private Long id;
    private String fileName;
    private String contentType;
    private String base64Data;
}
