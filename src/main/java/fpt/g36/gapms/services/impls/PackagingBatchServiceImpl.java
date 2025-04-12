package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.DyeRiskAssessment;
import fpt.g36.gapms.models.entities.PackagingBatch;
import fpt.g36.gapms.models.entities.PackagingRiskAssessment;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.PackagingBatchRepository;
import fpt.g36.gapms.repositories.PackagingRiskAssessmentRepository;
import fpt.g36.gapms.services.PackagingBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackagingBatchServiceImpl implements PackagingBatchService {

    @Autowired
    private PackagingBatchRepository packagingBatchRepository;

    @Autowired
    private PackagingRiskAssessmentRepository packagingRiskAssessmentRepository;

    @Override
    public List<PackagingBatch> getAllPackagingBatchForPackagingLead(Long psId) {
        List<PackagingBatch> packagingBatches = packagingBatchRepository.getAllWindingBatchByPackagingStageId(psId);
        return packagingBatches;
    }

    @Override
    public void changeStatusPackagingBatchInProcess(Long pbId, User leader) {
        PackagingBatch packagingBatch = packagingBatchRepository.findById(pbId).orElseThrow(() -> new RuntimeException("pbId not found"));
        packagingBatch.setWorkStatus(WorkEnum.IN_PROGRESS);
        packagingBatch.getPackagingStage().setWorkStatus(WorkEnum.IN_PROGRESS);
        packagingBatch.setLeaderStart(leader);
        packagingBatchRepository.save(packagingBatch);
    }

    @Override
    public void changeStatusPackagingBatchFinish(Long pbId, String photo, User leader) {
        PackagingBatch packagingBatch = packagingBatchRepository.findById(pbId).orElseThrow(() -> new RuntimeException("pbId not found"));
        packagingBatch.setWorkStatus(WorkEnum.FINISHED);
        packagingBatch.setTestStatus(TestEnum.TESTING);
        packagingBatch.setPackagingPhoto(photo);
        packagingBatch.setLeaderEnd(leader);
        packagingBatchRepository.save(packagingBatch);

        PackagingRiskAssessment packagingRiskAssessment = new PackagingRiskAssessment();
        packagingRiskAssessment.setPackagingBatch(packagingBatch);
        packagingRiskAssessmentRepository.save(packagingRiskAssessment);
    }

    @Override
    public PackagingBatch getPackagingBatchById(Long pbId) {
        PackagingBatch packagingBatch = packagingBatchRepository.findById(pbId).orElseThrow(() -> new RuntimeException("pbId not found"));
        return packagingBatch;

    }
}
