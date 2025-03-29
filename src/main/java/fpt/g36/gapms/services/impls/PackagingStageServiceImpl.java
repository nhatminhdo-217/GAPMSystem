package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.PackagingStage;
import fpt.g36.gapms.models.entities.WindingStage;
import fpt.g36.gapms.repositories.PackagingStageRepository;
import fpt.g36.gapms.repositories.WindingStageRepository;
import fpt.g36.gapms.services.PackagingStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackagingStageServiceImpl implements PackagingStageService {
    @Autowired
    private PackagingStageRepository packagingStageRepository;

    @Override
    public List<PackagingStage> getAllPackagingStageForPackagingLead(Long woId) {
        List<PackagingStage> windingStages = packagingStageRepository.getAllPackagingStageByWorkOrderId(woId);
        return windingStages;
    }

    @Override
    public void changeStatusPackagingStageInProcess(Long psId) {
        PackagingStage packagingStage = packagingStageRepository.findById(psId).orElseThrow(() -> new RuntimeException("psId not found"));

        packagingStage.setWorkStatus(WorkEnum.IN_PROGRESS);
        packagingStageRepository.save(packagingStage);
    }

    @Override
    public void changeStatusPackagingStageFinish(Long psId, String photo) {
        PackagingStage packagingStage = packagingStageRepository.findById(psId).orElseThrow(() -> new RuntimeException("psId not found"));
        packagingStage.setWorkStatus(WorkEnum.FINISHED);
        packagingStageRepository.save(packagingStage);
    }

    @Override
    public PackagingStage getPackagingStageById(Long psId) {
        PackagingStage packagingStage = packagingStageRepository.findById(psId).orElseThrow(() -> new RuntimeException("psId not found"));
        return packagingStage;
    }
}
