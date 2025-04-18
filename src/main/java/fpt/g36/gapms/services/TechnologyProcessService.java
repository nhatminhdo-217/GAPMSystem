package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.TechnologyProcess;

public interface TechnologyProcessService {

    TechnologyProcess getByDyeId(Long dyeId);
}
