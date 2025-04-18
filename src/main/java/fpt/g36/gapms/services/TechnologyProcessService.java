package fpt.g36.gapms.services;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.entities.DyeType;
import fpt.g36.gapms.models.entities.TechnologyProcess;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.entities.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface TechnologyProcessService {
    List<TechnologyProcess> createTechnologyProcess(Long workOrderId,
                                                    Long workOrderDetailId,
                                                    List<DyeType> dyeTypesForFirstBatches,
                                                    List<DyeType> dyeTypesForLastBatch,
                                                    BigDecimal dispergatorNForFirstBatches,
                                                    BigDecimal dispergatorNForLastBatch);

    TechnologyProcess getTechnologyProcessByIdAndCreatedBy(Long technicalProcessId, User createBy);

    Page<TechnologyProcess> getAllTechnologyProcessesByCreatedBy(Pageable pageable, User createBy);

    Page<TechnologyProcess> getTechnicalProcessByStatusAndCreatedBy(SendEnum status, Pageable pageable, User createBy);
}
