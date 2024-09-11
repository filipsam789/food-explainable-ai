package com.ai.explainableanalysis.model.Requests;

import lombok.Data;

import java.util.List;

@Data
public class VisualizationRequest {
    private List<String> features;
    private String plot_type;
    private String type_model;

    public VisualizationRequest(List<String> features, String plotType, String typeModel) {
        this.features = features;
        this.plot_type = plotType;
        this.type_model = typeModel;
    }

}
