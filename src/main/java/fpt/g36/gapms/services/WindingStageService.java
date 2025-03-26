package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.WindingStage;

import java.util.List;

public interface WindingStageService {
    List<WindingStage> getAllWindingStageForWindingLead(Long woId);

    void changeStatusWindingStageInProcess(Long wdId);

    void changeStatusWindingStageFinish(Long wdId, String photo);

    WindingStage getWindingStageById(Long wdId);
}
