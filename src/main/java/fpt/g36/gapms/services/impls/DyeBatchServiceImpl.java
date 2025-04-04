package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.DyeBatch;
import fpt.g36.gapms.models.entities.DyeRiskAssessment;
import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.DyeBatchRepository;
import fpt.g36.gapms.repositories.DyeRiskAssessmentRepository;
import fpt.g36.gapms.services.DyeBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DyeBatchServiceImpl implements DyeBatchService {

    @Autowired
    private DyeBatchRepository dyeBatchRepository;

    @Autowired
    private DyeRiskAssessmentRepository dyeRiskAssessmentRepository;
    @Override
    public List<DyeBatch> getAllDyeBatchForDyeLead(Long dsId) {
        List<DyeBatch> dyeBatches = dyeBatchRepository.getAllDyeBatchByDyeStageId(dsId);
        return dyeBatches;
    }

    @Override
    public void changeStatusDyeBatchInProcess(Long dbId, User leader) {
        DyeBatch dyeBatch = dyeBatchRepository.findById(dbId).orElseThrow(() -> new RuntimeException("dbId not found"));

        dyeBatch.setWorkStatus(WorkEnum.IN_PROGRESS);
        dyeBatch.getDyeStage().getWorkOrderDetail().getWorkOrder().setIsProduction(WorkEnum.IN_PROGRESS);
        dyeBatch.getDyeStage().setWorkStatus(WorkEnum.IN_PROGRESS);

            dyeBatch.setLeaderStart(leader);
        dyeBatchRepository.save(dyeBatch);
    }

    @Override
    public void changeStatusDyeBatchFinish(Long dbId, String photo, User leader) {
        DyeBatch dyeBatch = dyeBatchRepository.findById(dbId).orElseThrow(() -> new RuntimeException("dbId not found"));
        dyeBatch.setWorkStatus(WorkEnum.FINISHED);
        dyeBatch.setDyePhoto(photo);
        dyeBatch.setLeaderEnd(leader);
        dyeBatch.setTestStatus(TestEnum.TESTING);
        dyeBatchRepository.save(dyeBatch);

        DyeRiskAssessment dyeRiskAssessment = new DyeRiskAssessment();
        dyeRiskAssessment.setDyeBatch(dyeBatch);
        dyeRiskAssessmentRepository.save(dyeRiskAssessment);
    }

    @Override
    public DyeBatch getDyeBatchById(Long dbId) {
        DyeBatch dyeBatch = dyeBatchRepository.findById(dbId).orElseThrow(() -> new RuntimeException("dbId not found"));
        return dyeBatch;
    }

}
