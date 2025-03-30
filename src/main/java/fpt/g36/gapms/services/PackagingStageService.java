package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.PackagingStage;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.entities.WindingStage;

import java.util.List;

public interface PackagingStageService {

    List<PackagingStage> getAllPackagingStageForPackagingLead(Long woId);

    void changeStatusPackagingStageInProcess(Long psId, User leader);

    void changeStatusPackagingStageFinish(Long psId, String photo, User leader);

    PackagingStage getPackagingStageById(Long psId);
}
