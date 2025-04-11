package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.PhotoStage;
import fpt.g36.gapms.repositories.PhotoStageRepository;
import fpt.g36.gapms.services.PhotoStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoStageServiceImpl implements PhotoStageService {

    @Autowired
    private PhotoStageRepository photoStageRepository;
    @Override
    public List<PhotoStage> getAllPhotoStageByDraId(Long draId) {
        List<PhotoStage> photoStages = photoStageRepository.getAllPhotoStageByDyeRiskId(draId);
        return photoStages;
    }

    @Override
    public List<PhotoStage> getAllPhotoStageByWraId(Long wraId) {
        List<PhotoStage> photoStages = photoStageRepository.getAllPhotoStageByWindingRiskId(wraId);
        return photoStages;
    }

    @Override
    public List<PhotoStage> getAllPhotoStageByPraId(Long praId) {
        List<PhotoStage> photoStages = photoStageRepository.getAllPhotoStageByPackagingRiskId(praId);
        return photoStages;
    }
}
