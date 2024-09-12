package com.ai.explainableanalysis.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;

@Data
@Entity
@NoArgsConstructor
@Table(name = "visualization_data")
public class VisualizationData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Lob
    private String imageData;
    @OneToOne(mappedBy = "visualization", cascade = CascadeType.ALL)
    private ShapCache shapCache;

    public VisualizationData(String imageData, ShapCache shapCache) {
        this.imageData = imageData;
        this.shapCache = shapCache;
    }

}
