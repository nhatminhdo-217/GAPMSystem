package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.TechnologyProcess;
import fpt.g36.gapms.repositories.TechnologyProcessRepository;
import fpt.g36.gapms.services.TechnologyProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TechnologyProcessServiceImpl implements TechnologyProcessService {

    @Autowired
    private TechnologyProcessRepository technologyProcessRepository;

    @Override
    public TechnologyProcess getByDyeId(Long dyeId) {
        TechnologyProcess  technologyProcess = technologyProcessRepository.getTechnologyProcessByBatchId(dyeId);
        return technologyProcess;
    }
}
