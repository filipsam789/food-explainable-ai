package com.ai.explainableanalysis.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
@Entity
@Data
@NoArgsConstructor
@Table(name = "shap_cache")
public class ShapCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String modelType;
    @OneToOne
    @JoinColumn(name = "input", referencedColumnName = "Id", unique = true)
    private InputParameters input;
    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key column
    private User user;
    @OneToOne
    private VisualizationData visualization;
    @JoinColumn(name = "visualization", referencedColumnName = "Id", unique = true)
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isValid;

    public ShapCache(String modelType, InputParameters input, VisualizationData visualization) {
        this.modelType = modelType;
        this.input = input;
        this.visualization = visualization;
        this.isValid = true;
        this.createdAt=LocalDateTime.now();
        this.expiresAt = LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfNextMonth());
    }


}

