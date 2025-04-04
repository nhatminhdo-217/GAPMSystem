package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.DyeBatch;
import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.User;

import java.util.List;

public interface DyeBatchService {

    List<DyeBatch> getAllDyeBatchForDyeLead(Long dsId);

    void changeStatusDyeBatchInProcess(Long dbId, User leader);

    void changeStatusDyeBatchFinish(Long dbId, String photo, User leader);

    DyeBatch getDyeBatchById(Long dbId);
}
