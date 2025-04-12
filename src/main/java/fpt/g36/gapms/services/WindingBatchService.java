package fpt.g36.gapms.services;


import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.entities.WindingBatch;
import fpt.g36.gapms.models.entities.WindingStage;

import java.util.List;

public interface WindingBatchService {

    List<WindingBatch> getAllWindingBatchForWindingLead(Long wsId);

    void changeStatusWindingBatchInProcess(Long wbId, User leader);

    void changeStatusWindingBatchFinish(Long wbId, String photo, User leader);

    WindingBatch getWindingBatchById(Long wbId);
}
