package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.PackagingBatch;
import fpt.g36.gapms.models.entities.PackagingStage;
import fpt.g36.gapms.models.entities.User;

import java.util.List;

public interface PackagingBatchService {

    List<PackagingBatch> getAllPackagingBatchForPackagingLead(Long psId);

    void changeStatusPackagingBatchInProcess(Long pbId, User leader);

    void changeStatusPackagingBatchFinish(Long pbId, String photo, User leader);

    PackagingBatch getPackagingBatchById(Long pbId);
}
