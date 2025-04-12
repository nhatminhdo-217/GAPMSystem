package fpt.g36.gapms.services;


import fpt.g36.gapms.models.entities.RiskSolution;
import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RiskSolutionService {

    Page<RiskSolution> getAllRiskSolution(Pageable pageable);

    RiskSolution getRiskSolutionById(Long riskSolutionId);

    RiskSolution saveRiskSolution(Long rsId, RiskSolution riskSolution, User user);

    Page<RiskSolution> getAllRiskSolutionManager(Pageable pageable);

    RiskSolution approveRiskSolution(Long rsId);
}
