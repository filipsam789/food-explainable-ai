package com.ai.explainableanalysis.service;

import com.ai.explainableanalysis.model.ShapCache;
import com.ai.explainableanalysis.model.VisualizationData;

import java.util.List;

public interface ShapService {
    public void saveVisualizations(String modelType, List<String> features, String plotType, String imageData);
    public List<ShapCache> getUserLatestVisualizations(String username);
    public String getVisualizationByInput(List<String> features, String modelType, String plotType);
}
