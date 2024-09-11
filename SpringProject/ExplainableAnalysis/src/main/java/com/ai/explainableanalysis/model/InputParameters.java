package com.ai.explainableanalysis.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "input_parameters")
public class InputParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String modelType;
    @ElementCollection
    private List<String> features;
    private String plotType;

    @OneToOne(mappedBy = "input", cascade = CascadeType.ALL)
    private ShapCache shapCache;

    public InputParameters( String modelType, List<String> features, String plot_type) {
        this.modelType = modelType;
        this.features = features;
        plotType = plot_type;
    }

}
