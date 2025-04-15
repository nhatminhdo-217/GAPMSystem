package fpt.g36.gapms.services.impls;


import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.repositories.WindingBatchRepository;
import fpt.g36.gapms.repositories.WindingRiskAssessmentRepository;
import fpt.g36.gapms.repositories.WindingStageRepository;
import fpt.g36.gapms.services.WindingBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WindingBatchServiceImpl implements WindingBatchService {

    @Autowired
    private WindingBatchRepository windingBatchRepository;
    @Autowired
    private WindingRiskAssessmentRepository windingRiskAssessmentRepository;

    @Override
    public List<WindingBatch> getAllWindingBatchForWindingLead(Long wsId) {
        List<WindingBatch> windingBatches = windingBatchRepository.getAllWindingBatchByWindingStageId(wsId);
        return windingBatches;
    }

    @Override
    public void changeStatusWindingBatchInProcess(Long wbId, User leader) {
        WindingBatch windingBatch = windingBatchRepository.findById(wbId).orElseThrow(() -> new RuntimeException("wbId not found"));
        windingBatch.setWorkStatus(WorkEnum.IN_PROGRESS);
        windingBatch.setPass(null);
        windingBatch.getWindingStage().setWorkStatus(WorkEnum.IN_PROGRESS);
        windingBatch.setLeaderStart(leader);
        windingBatchRepository.save(windingBatch);
    }

    @Override
    public void changeStatusWindingBatchFinish(Long wbId, String photo, User leader) {
        WindingBatch windingBatch = windingBatchRepository.findById(wbId).orElseThrow(() -> new RuntimeException("wbId not found"));
        windingBatch.setWorkStatus(WorkEnum.FINISHED);
        windingBatch.setTestStatus(TestEnum.TESTING);
        windingBatch.setWindingPhoto(photo);
        windingBatch.setLeaderEnd(leader);
        windingBatchRepository.save(windingBatch);

        WindingRiskAssessment windingRiskAssessment = new WindingRiskAssessment();
        windingRiskAssessment.setWindingBatch(windingBatch);
        windingRiskAssessmentRepository.save(windingRiskAssessment);
    }

    @Override
    public WindingBatch getWindingBatchById(Long wbId) {
        WindingBatch windingBatch = windingBatchRepository.findById(wbId).orElseThrow(() -> new RuntimeException("wbId not found"));
        return windingBatch;

    }
}
