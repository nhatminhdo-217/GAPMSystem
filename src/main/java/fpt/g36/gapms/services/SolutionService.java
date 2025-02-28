package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.SolutionDTO;
import fpt.g36.gapms.models.entities.Solution;
import org.springframework.stereotype.Service;

@Service
public interface SolutionService {
    Solution addSolution(Long rfqId, Long userId, SolutionDTO solutionDTO);
}
