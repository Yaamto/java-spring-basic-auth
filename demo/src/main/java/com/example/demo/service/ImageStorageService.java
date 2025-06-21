package com.example.demo.service;

import com.example.demo.model.PropertyImage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class ImageStorageService {

    public PropertyImage createPropertyImage(MultipartFile file) throws IOException {
        PropertyImage image = new PropertyImage();
        image.setFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setData(file.getBytes());
        return image;
    }

    public String getBase64Data(PropertyImage image) {
        return Base64.getEncoder().encodeToString(image.getData());
    }
}
