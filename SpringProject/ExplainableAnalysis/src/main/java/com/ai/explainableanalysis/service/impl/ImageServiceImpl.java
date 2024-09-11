package com.ai.explainableanalysis.service.impl;

import com.ai.explainableanalysis.service.ImageService;
import org.springframework.stereotype.Service;

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
        String fileName = UUID.randomUUID() + ".png";
        File outputFile = new File(IMAGE_SAVE_DIR, fileName);

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(imageBytes);
        }

        return fileName;
    }
}
