package com.ai.explainableanalysis.repository;

import com.ai.explainableanalysis.model.InputParameters;
import com.ai.explainableanalysis.model.VisualizationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisualizationDataRepository extends JpaRepository<VisualizationData, Long> {
}
