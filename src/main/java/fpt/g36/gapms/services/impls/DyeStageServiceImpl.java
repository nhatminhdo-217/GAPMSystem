package fpt.g36.gapms.services.impls;


import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.repositories.DyeStageRepository;
import fpt.g36.gapms.services.DyeStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DyeStageServiceImpl implements DyeStageService {

    @Autowired
    private DyeStageRepository dyeStageRepository;

    @Override
    public List<DyeStage> getAllDyeStageForDyeLead(Long woId) {
        List<DyeStage> dyeStages = dyeStageRepository.getAllDyeStageByWorkOrderId(woId);
        return dyeStages;
    }

    @Override
    public void changeStatusDyeStageInProcess(Long dyeId) {

        DyeStage dyeStage = dyeStageRepository.findById(dyeId).orElseThrow(() -> new RuntimeException("dyeId not found"));

            dyeStage.setWorkStatus(WorkEnum.IN_PROGRESS);
            dyeStage.getWorkOrderDetail().getWorkOrder().setIsProduction(WorkEnum.IN_PROGRESS);
            dyeStage.setTestStatus(TestEnum.TESTING);
            dyeStageRepository.save(dyeStage);

    }

    @Override
    public void changeStatusDyeStageFinish(Long dyeId, String photo) {
        DyeStage dyeStage = dyeStageRepository.findById(dyeId).orElseThrow(() -> new RuntimeException("dyeId not found"));
        dyeStage.setWorkStatus(WorkEnum.FINISHED);
        dyeStage.setDyePhoto(photo);
        dyeStageRepository.save(dyeStage);
    }

    @Override
    public DyeStage getDyeStageById(Long dyeId) {
        DyeStage dyeStage = dyeStageRepository.findById(dyeId).orElseThrow(() -> new RuntimeException("dyeId not found"));
        return dyeStage;
    }
}
