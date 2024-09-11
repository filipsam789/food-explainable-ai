package com.ai.explainableanalysis.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ai.explainableanalysis.model.InputParameters;

@Repository
public interface InputParametersRepository  extends JpaRepository<InputParameters,Long> {
}
