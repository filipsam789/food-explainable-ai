package com.ai.explainableanalysis.service.impl;

import com.ai.explainableanalysis.service.ImageService;
import org.springframework.stereotype.Service;

import java.awt.image.ImageConsumer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private static final String IMAGE_SAVE_DIR = "src/main/resources/static/images/";

    @Override
    public String saveImage(String base64Image) throws IOException {
        // Generate a unique file name
        String fileName = UUID.randomUUID().toString() + ".png";
        File outputFile = new File(IMAGE_SAVE_DIR, fileName);

        // Decode Base64 image and save it to the file
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(imageBytes);
        }

        // Return the relative path to save in the database
        return fileName;
    }
}
