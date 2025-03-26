package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.PackagingStage;
import fpt.g36.gapms.models.entities.WindingStage;

import java.util.List;

public interface PackagingStageService {

    List<PackagingStage> getAllPackagingStageForPackagingLead(Long woId);

    void changeStatusPackagingStageInProcess(Long psId);

    void changeStatusPackagingStageFinish(Long psId, String photo);

    PackagingStage getPackagingStageById(Long psId);
}
