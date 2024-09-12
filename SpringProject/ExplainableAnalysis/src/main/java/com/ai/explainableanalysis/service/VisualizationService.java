package com.ai.explainableanalysis.service;

import com.ai.explainableanalysis.model.VisualizationData;

import java.io.IOException;

public interface VisualizationService {
    public VisualizationData saveImage(String base64Image) throws IOException;
}
