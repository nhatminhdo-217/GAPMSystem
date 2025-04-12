package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.RiskSolution;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RiskSolutionRepository;
import fpt.g36.gapms.services.RiskSolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RiskSolutionServiceImpl implements RiskSolutionService {
    @Autowired
    private RiskSolutionRepository riskSolutionRepository;
    @Override
    public Page<RiskSolution> getAllRiskSolution(Pageable pageable) {
        Page<RiskSolution> riskSolutions = riskSolutionRepository.getAllRiskSolution(pageable);
        return riskSolutions;
    }

    @Override
    public RiskSolution getRiskSolutionById(Long riskSolutionId) {
        RiskSolution riskSolution = riskSolutionRepository.findById(riskSolutionId).orElseThrow(() -> new RuntimeException("RiskSolution not found"));
        return riskSolution;
    }

    @Override
    public RiskSolution saveRiskSolution(Long rsId, RiskSolution riskSolution, User user) {
        RiskSolution riskSolution_save = riskSolutionRepository.findById(rsId).orElseThrow(() -> new RuntimeException("RiskSolution not found"));
        riskSolution_save.setWeightNeed(riskSolution.getWeightNeed());
        riskSolution_save.setNote(riskSolution.getNote());
        riskSolution_save.setApproveStatus(BaseEnum.WAIT_FOR_APPROVAL);
        riskSolution_save.setCreatedBy(user);
        riskSolutionRepository.save(riskSolution_save);
        return riskSolution_save;
    }

    @Override
    public Page<RiskSolution> getAllRiskSolutionManager(Pageable pageable) {
        Page<RiskSolution> riskSolutions = riskSolutionRepository.getAllRiskSolutionManager(pageable);
        return riskSolutions;
    }

    @Override
    public RiskSolution approveRiskSolution(Long rsId) {
        RiskSolution riskSolution = riskSolutionRepository.findById(rsId).orElseThrow(() -> new RuntimeException("RiskSolution not found"));
        riskSolution.setApproveStatus(BaseEnum.APPROVED);
        if(riskSolution.getDyeRiskAssessment() != null){
            riskSolution.getDyeRiskAssessment().getDyeBatch().setWorkStatus(WorkEnum.FIX);
            riskSolution.getDyeRiskAssessment().getDyeBatch().setDyePhoto(null);
            riskSolutionRepository.save(riskSolution);
        }

        if(riskSolution.getWindingRiskAssessment() != null){
            riskSolution.getWindingRiskAssessment().getWindingBatch().getDyeBatch().setWorkStatus(WorkEnum.FIX);
            riskSolution.getWindingRiskAssessment().getWindingBatch().getDyeBatch().setDyePhoto(null);
            riskSolution.getWindingRiskAssessment().getWindingBatch().getDyeBatch().setPass(null);
            riskSolution.getWindingRiskAssessment().getWindingBatch().getDyeBatch().getDyeStage().setWorkStatus(WorkEnum.IN_PROGRESS);
            riskSolution.getWindingRiskAssessment().getWindingBatch().setWorkStatus(WorkEnum.FIX);
            riskSolution.getWindingRiskAssessment().getWindingBatch().setWindingPhoto(null);
            riskSolutionRepository.save(riskSolution);
        }

        return riskSolution;
    }


}
