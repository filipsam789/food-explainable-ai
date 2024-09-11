package com.ai.explainableanalysis.service.impl;

import com.ai.explainableanalysis.model.InputParameters;
import com.ai.explainableanalysis.model.ShapCache;
import com.ai.explainableanalysis.model.VisualizationData;
import com.ai.explainableanalysis.repository.InputParametersRepository;
import com.ai.explainableanalysis.repository.ShapCacheRepository;
import com.ai.explainableanalysis.repository.VisualizationDataRepository;
import com.ai.explainableanalysis.service.ShapService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShapServiceImpl implements ShapService {
    private final InputParametersRepository inputParametersRepository;
    private final ShapCacheRepository shapCacheRepository;
    private final VisualizationDataRepository visualizationDataRepository;

    public ShapServiceImpl(InputParametersRepository inputParametersRepository, ShapCacheRepository shapCacheRepository, VisualizationDataRepository visualizationDataRepository) {
        this.inputParametersRepository = inputParametersRepository;
        this.shapCacheRepository = shapCacheRepository;
        this.visualizationDataRepository = visualizationDataRepository;
    }

    @Override
    public void saveVisualizations(String modelType, List<String> features, String plotType, String imageData) {
        InputParameters inputParameters = new InputParameters(modelType, features, plotType);
        inputParameters = inputParametersRepository.save(inputParameters);

        VisualizationData visualizationData = new VisualizationData(imageData, null);
        visualizationData = visualizationDataRepository.save(visualizationData);

        ShapCache shapCache = new ShapCache(modelType, inputParameters, visualizationData);
        shapCache.setVisualization(visualizationData);
        shapCache = shapCacheRepository.save(shapCache);

        visualizationData.setShapCache(shapCache);
        visualizationDataRepository.save(visualizationData);
    }

    @Override
    public List<ShapCache> getUserLatestVisualizations(String username) {
        return shapCacheRepository.findByUser_Username(username);
    }

    @Override
    public String getVisualizationByInput(List<String> features, String modelType, String plotType) {
        ShapCache result = shapCacheRepository.findByInput_FeaturesInAndInput_ModelTypeAndInput_PlotType(features,modelType,plotType);
        if(result == null){
            return "None";
        }
        return result.getVisualization().getImageData();
    }
}
