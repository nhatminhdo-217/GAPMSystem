package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.PhotoStage;

import java.util.List;

public interface PhotoStageService {

    List<PhotoStage> getAllPhotoStageByDraId(Long draId);

    List<PhotoStage> getAllPhotoStageByWraId(Long wraId);

    List<PhotoStage> getAllPhotoStageByPraId(Long praId);
}
