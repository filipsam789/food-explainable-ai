package com.ai.explainableanalysis.service;

import java.io.IOException;

public interface ImageService {
    public String saveImage(String base64Image) throws IOException;
}
