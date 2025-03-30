package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.DyeRiskAssessment;
import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DyeStageService {

    List<DyeStage> getAllDyeStageForDyeLead(Long woId);

    void changeStatusDyeStageInProcess(Long dyeId, User leader);

    void changeStatusDyeStageFinish(Long dyeId, String photo, User leader);


    DyeStage getDyeStageById(Long dyeId);

    DyeRiskAssessment getDyeRiskAssessmentByDyeStageId(Long dyeId);

    DyeRiskAssessment saveTestDye(Long id, DyeRiskAssessment dyeRiskAssessment, User qaDye, MultipartFile[] photos) throws IOException;
}
