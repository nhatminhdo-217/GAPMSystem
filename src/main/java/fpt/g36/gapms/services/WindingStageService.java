package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface WindingStageService {
    List<WindingStage> getAllWindingStageForWindingLead(Long woId);


    WindingStage getWindingStageById(Long wdId);

    WindingRiskAssessment getWindingRiskAssessmentByWindingBatchId(Long wbId);

    WindingRiskAssessment saveTestWingding(Long id, WindingRiskAssessment windingRiskAssessment, User qaWinding, MultipartFile[] photos) throws IOException;
}
