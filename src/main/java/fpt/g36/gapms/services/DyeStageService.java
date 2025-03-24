package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.DyeStage;

import java.util.List;

public interface DyeStageService {

    List<DyeStage> getAllDyeStageForDyeLead(Long woId);

    void changeStatusDyeStageInProcess(Long dyeId);

    void changeStatusDyeStageFinish(Long dyeId, String photo);


    DyeStage getDyeStageById(Long dyeId);
}
