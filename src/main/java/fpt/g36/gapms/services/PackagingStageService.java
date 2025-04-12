package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PackagingStageService {

    List<PackagingStage> getAllPackagingStageForPackagingLead(Long woId);


    PackagingStage getPackagingStageById(Long psId);

    PackagingRiskAssessment getPackagingRiskAssessmentByPackagingBatchId(Long pbId);

    PackagingRiskAssessment saveTestPackaging(Long id, PackagingRiskAssessment packagingRiskAssessment, User qaPackaging, MultipartFile[] photos) throws IOException;
}
