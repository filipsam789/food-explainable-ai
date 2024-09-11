package com.ai.explainableanalysis.repository;

import com.ai.explainableanalysis.model.ShapCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShapCacheRepository extends JpaRepository<ShapCache,Long> {
    List<ShapCache> findByUser_Username(String username);
    ShapCache findByInput_FeaturesInAndInput_ModelTypeAndInput_PlotType(List<String> features, String modelType, String plotType);
}
