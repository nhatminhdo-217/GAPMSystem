package fpt.g36.gapms.services;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;

import fpt.g36.gapms.models.dto.dye_technical.DyeTypeDTO;
import fpt.g36.gapms.models.dto.dye_technical.TechnologyProcessForm;
import fpt.g36.gapms.models.entities.TechnologyProcess;
import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;

@Service
public interface TechnologyProcessService {

    TechnologyProcess getTechnologyProcessById(Long technicalProcessId);

    @Transactional
    List<TechnologyProcess> createTechnologyProcess(User currentUser,
                                                    Long workOrderId,
                                                    Long workOrderDetailId,
                                                    List<DyeTypeDTO> dyeTypesForFirstBatchesDTO,
                                                    List<DyeTypeDTO> dyeTypesForLastBatchDTO,
                                                    BigDecimal dispergatorNForFirstBatches,
                                                    BigDecimal dispergatorNForLastBatch,
                                                    BigDecimal dfmForFirstBatches,
                                                    BigDecimal dfmForLastBatch,
                                                    BigDecimal anbatexForFirstBatches,
                                                    BigDecimal anbatexForLastBatch);

    @Transactional
    List<TechnologyProcess> updateTechnologyProcess(Long workOrderId,
                                                    Long workOrderDetailId,
                                                    List<DyeTypeDTO> dyeTypesForFirstBatchesDTO,
                                                    List<DyeTypeDTO> dyeTypesForLastBatchDTO,
                                                    BigDecimal dispergatorNForFirstBatches,
                                                    BigDecimal dispergatorNForLastBatch,
                                                    BigDecimal dfmForFirstBatches,
                                                    BigDecimal dfmForLastBatch,
                                                    BigDecimal anbatexForFirstBatches,
                                                    BigDecimal anbatexForLastBatch,
                                                    User currentUser);

    void submitTechnologyProcesses(Long workOrderId);

    Page<TechnologyProcess> getAllTechnologyProcessesByCreatedBy(Pageable pageable, User createBy);

    TechnologyProcess getByDyeId(Long dyeId);

    Page<TechnologyProcess> getTechnologyProcessesByStatusAndCreatedBy(BaseEnum status, Pageable pageable, User createdBy);

    TechnologyProcess getTechnologyProcessByIdAndCreatedBy(Long id, User createdBy);

}
