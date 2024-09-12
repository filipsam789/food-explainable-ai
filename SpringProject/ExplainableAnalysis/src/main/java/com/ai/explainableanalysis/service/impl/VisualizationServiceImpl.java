package com.ai.explainableanalysis.service.impl;

import com.ai.explainableanalysis.model.VisualizationData;
import com.ai.explainableanalysis.repository.VisualizationDataRepository;
import com.ai.explainableanalysis.service.VisualizationService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
public class VisualizationServiceImpl implements VisualizationService {

    private final VisualizationDataRepository visualizationDataRepository;

    public VisualizationServiceImpl(VisualizationDataRepository visualizationDataRepository) {
        this.visualizationDataRepository = visualizationDataRepository;
    }

    @Override
    public VisualizationData saveImage(String base64Image) throws IOException {

        VisualizationData entity = new VisualizationData(base64Image,null);
        entity = visualizationDataRepository.save(entity);


        return entity;
    }
}
