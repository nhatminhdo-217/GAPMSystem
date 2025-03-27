package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.WindingStage;
import fpt.g36.gapms.repositories.WindingStageRepository;
import fpt.g36.gapms.services.WindingStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WindingStageServiceImpl implements WindingStageService {
    @Autowired
    private WindingStageRepository windingStageRepository;

    @Override
    public List<WindingStage> getAllWindingStageForWindingLead(Long woId) {
        List<WindingStage> windingStages = windingStageRepository.getAllWindingStageByWorkOrderId(woId);
        return windingStages;
    }

    @Override
    public void changeStatusWindingStageInProcess(Long wdId) {
        WindingStage windingStage = windingStageRepository.findById(wdId).orElseThrow(() -> new RuntimeException("wdId not found"));

        windingStage.setWorkStatus(WorkEnum.IN_PROGRESS);
        windingStage.setTestStatus(TestEnum.TESTING);
        windingStageRepository.save(windingStage);
    }

    @Override
    public void changeStatusWindingStageFinish(Long wdId, String photo) {
        WindingStage windingStage = windingStageRepository.findById(wdId).orElseThrow(() -> new RuntimeException("wdId not found"));
        windingStage.setWorkStatus(WorkEnum.FINISHED);
        windingStage.setWindingPhoto(photo);
        windingStageRepository.save(windingStage);
    }

    @Override
    public WindingStage getWindingStageById(Long wdId) {
        WindingStage windingStage = windingStageRepository.findById(wdId).orElseThrow(() -> new RuntimeException("wdId not found"));
        return windingStage;
    }
}
