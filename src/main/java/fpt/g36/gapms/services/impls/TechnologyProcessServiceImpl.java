package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.TechnologyProcess;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.dto.dye_technical.DyeTypeDTO;
import fpt.g36.gapms.models.dto.dye_technical.TechnologyProcessForm;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.repositories.DyeTypeRepository;

import fpt.g36.gapms.repositories.TechnologyProcessRepository;
import fpt.g36.gapms.repositories.WorkOrderRepository;
import fpt.g36.gapms.services.TechnologyProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;


@Service
public class TechnologyProcessServiceImpl implements TechnologyProcessService {

    @Autowired
    private TechnologyProcessRepository technologyProcessRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private DyeTypeRepository dyeTypeRepository;

    // Tách logic tính toán thông số thành phương thức riêng
    private static class ProcessParameters {
        BigDecimal avcoLveDlxPlus;
        BigDecimal chelator;
        BigDecimal detergent;
        BigDecimal reducingAgent;
        BigDecimal dfm;
        BigDecimal axit;
        BigDecimal anbatex;
        BigDecimal liquorRatio;

        ProcessParameters(BigDecimal coneBatchWeight) {
            this.avcoLveDlxPlus = coneBatchWeight.multiply(BigDecimal.valueOf(0.012));
            this.chelator = coneBatchWeight.divide(BigDecimal.valueOf(2.5), 2, BigDecimal.ROUND_HALF_UP);
            this.detergent = coneBatchWeight.divide(BigDecimal.valueOf(1.2), 2, BigDecimal.ROUND_HALF_UP);
            this.reducingAgent = coneBatchWeight.divide(BigDecimal.valueOf(0.8), 2, BigDecimal.ROUND_HALF_UP);
            this.dfm = coneBatchWeight.multiply(BigDecimal.valueOf(0.012));
            this.axit = coneBatchWeight.divide(BigDecimal.valueOf(1.2), 2, BigDecimal.ROUND_HALF_UP);
            this.anbatex = coneBatchWeight.divide(BigDecimal.valueOf(1.5), 2, BigDecimal.ROUND_HALF_UP);
            this.liquorRatio = BigDecimal.valueOf(6);
        }
    }

    @Override
    public TechnologyProcess getTechnologyProcessById(Long technicalProcessId) {
        return technologyProcessRepository.getById(technicalProcessId);
    }

    private String generateQrCode(TechnologyProcess process) {
        String qrUrl = "https://gapms-aha3aphhh0fna2dx.southeastasia-01.azurewebsites.net/work-order/technology-process/" + process.getId();
        System.err.println("Lưu URL mã QR cho TechnologyProcess ID: " + process.getId() + ": " + qrUrl);
        return qrUrl;
    }

    private List<DyeType> convertToDyeTypeEntities(List<DyeTypeDTO> dyeTypeDTOs, TechnologyProcess technologyProcess) {
        List<DyeType> dyeTypes = new ArrayList<>();
        for (DyeTypeDTO dto : dyeTypeDTOs) {
            DyeType dyeType = new DyeType();
            dyeType.setName(dto.getName());
            dyeType.setRatio(dto.getRatio());
            dyeType.setLightPercent(dto.getLightPercent());
            dyeType.setWeight(dto.getWeight());
            dyeType.setTechnologyProcess(technologyProcess);
            dyeTypes.add(dyeType);
        }
        return dyeTypes;
    }

    private void validateDyeBatches(List<DyeBatch> dyeBatches, Long workOrderDetailId) {
        if (dyeBatches.isEmpty()) {
            throw new IllegalStateException("WorkOrderDetail ID: " + workOrderDetailId + " không có DyeBatch để tạo TechnologyProcess.");
        }

        boolean isBatchOneNotStarted = dyeBatches.stream()
                .filter(batch -> batch.getBatchNumber() == 1)
                .allMatch(batch -> batch.getWorkStatus() == WorkEnum.NOT_STARTED);

        if (!isBatchOneNotStarted) {
            throw new IllegalStateException("Tất cả DyeBatch với batchNumber = 1 trong WorkOrderDetail ID: " + workOrderDetailId + " phải ở trạng thái NOT_STARTED để tạo TechnologyProcess.");
        }
    }

    private void validateDyeTypes(List<DyeTypeDTO> dyeTypesForFirstBatchesDTO, List<DyeTypeDTO> dyeTypesForLastBatchDTO, int totalBatches) {
        if (dyeTypesForFirstBatchesDTO == null || dyeTypesForFirstBatchesDTO.isEmpty()) {
            throw new IllegalArgumentException("dyeTypesForFirstBatches không được rỗng.");
        }
        if (totalBatches > 1 && (dyeTypesForLastBatchDTO == null || dyeTypesForLastBatchDTO.isEmpty())) {
            throw new IllegalArgumentException("dyeTypesForLastBatch không được rỗng khi có nhiều hơn 1 mẻ.");
        }
    }

    @Transactional
    public List<TechnologyProcess> createTechnologyProcess(User currentUser,
                                                           Long workOrderId,
                                                           Long workOrderDetailId,
                                                           List<DyeTypeDTO> dyeTypesForFirstBatchesDTO,
                                                           List<DyeTypeDTO> dyeTypesForLastBatchDTO,
                                                           BigDecimal dispergatorNForFirstBatches,
                                                           BigDecimal dispergatorNForLastBatch) {
        try {
            System.err.println("Bắt đầu tạo TechnologyProcess cho WorkMISOrder ID: " + workOrderId + ", WorkOrderDetail ID: " + workOrderDetailId);
            WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy WorkOrder với ID: " + workOrderId));
            System.err.println("Đã tìm thấy WorkOrder ID: " + workOrderId);

            if (workOrder.getStatus() != BaseEnum.APPROVED) {
                throw new IllegalStateException("WorkOrder phải ở trạng thái APPROVED để tạo TechnologyProcess. Trạng thái hiện tại: " + workOrder.getStatus());
            }
            System.err.println("WorkOrder ID: " + workOrderId + " có trạng thái APPROVED, tiếp tục xử lý.");

            WorkOrderDetail workOrderDetail = workOrder.getWorkOrderDetails().stream()
                    .filter(detail -> detail.getId().equals(workOrderDetailId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy WorkOrderDetail với ID: " + workOrderDetailId + " trong WorkOrder ID: " + workOrderId));
            System.err.println("Đã tìm thấy WorkOrderDetail ID: " + workOrderDetailId);

            List<DyeBatch> dyeBatches = workOrderDetail.getDyeStage().getDyebatches();
            System.err.println("Số lượng DyeBatch trong WorkOrderDetail ID: " + workOrderDetailId + " là: " + dyeBatches.size());

            dyeBatches.sort(Comparator.comparingInt(DyeBatch::getBatchNumber));
            validateDyeBatches(dyeBatches, workOrderDetailId);
            System.err.println("Tất cả DyeBatch với batchNumber = 1 trong WorkOrderDetail ID: " + workOrderDetailId + " có trạng thái NOT_STARTED, tiếp tục xử lý.");

            int totalBatches = dyeBatches.size();
            System.err.println("Sắp xếp DyeBatch hoàn tất, tổng số mẻ: " + totalBatches);

            validateDyeTypes(dyeTypesForFirstBatchesDTO, dyeTypesForLastBatchDTO, totalBatches);
            System.err.println("Đã kiểm tra dyeTypes: dyeTypesForFirstBatches có " + dyeTypesForFirstBatchesDTO.size() + " phần tử, dyeTypesForLastBatch có " + (dyeTypesForLastBatchDTO != null ? dyeTypesForLastBatchDTO.size() : 0) + " phần tử.");

            List<TechnologyProcess> technologyProcesses = new ArrayList<>();

            if (totalBatches > 1) {
                System.err.println("Trường hợp có nhiều mẻ (" + totalBatches + " mẻ): các mẻ đầu giống nhau, mẻ cuối khác.");
                BigDecimal coneBatchWeightFirst = dyeBatches.get(0).getCone_batch_weight();
                if (coneBatchWeightFirst == null) {
                    throw new IllegalStateException("cone_batch_weight của DyeBatch đầu tiên không được null.");
                }
                System.err.println("cone_batch_weight của mẻ đầu tiên: " + coneBatchWeightFirst);

                ProcessParameters paramsFirst = new ProcessParameters(coneBatchWeightFirst);
                System.err.println("Thông số TechnologyProcess cho các mẻ đầu: avcoLveDlxPlus=" + paramsFirst.avcoLveDlxPlus + ", chelator=" + paramsFirst.chelator + ", detergent=" + paramsFirst.detergent +
                        ", reducingAgent=" + paramsFirst.reducingAgent + ", dfm=" + paramsFirst.dfm + ", axit=" + paramsFirst.axit + ", anbatex=" + paramsFirst.anbatex + ", liquorRatio=" + paramsFirst.liquorRatio +
                        ", dispergatorN=" + dispergatorNForFirstBatches);

                for (int i = 0; i < totalBatches - 1; i++) {
                    DyeBatch batch = dyeBatches.get(i);
                    TechnologyProcess processForBatch = new TechnologyProcess();
                    processForBatch.setCreatedBy(currentUser);
                    processForBatch.setCreateAt(LocalDateTime.now());
                    processForBatch.setUpdateAt(LocalDateTime.now());
                    processForBatch.setAvcoLveDlxPlus(paramsFirst.avcoLveDlxPlus);
                    processForBatch.setChelator(paramsFirst.chelator);
                    processForBatch.setDetergent(paramsFirst.detergent);
                    processForBatch.setReducingAgent(paramsFirst.reducingAgent);
                    processForBatch.setDfm(paramsFirst.dfm);
                    processForBatch.setAxit(paramsFirst.axit);
                    processForBatch.setAnbatex(paramsFirst.anbatex);
                    processForBatch.setLiquorRatio(paramsFirst.liquorRatio);
                    processForBatch.setDispergatorN(dispergatorNForFirstBatches);
                    processForBatch.setDyeBatch(batch);
                    processForBatch.setSendStatus(SendEnum.NOT_SENT);
                    processForBatch.setStatus(BaseEnum.DRAFT);
                    processForBatch.setDyeTypes(new ArrayList<>());

                    TechnologyProcess savedProcess = technologyProcessRepository.save(processForBatch);
                    List<DyeType> dyeTypesForFirstBatches = convertToDyeTypeEntities(dyeTypesForFirstBatchesDTO, savedProcess);
                    savedProcess.setDyeTypes(dyeTypesForFirstBatches);
                    savedProcess = technologyProcessRepository.save(savedProcess);
                    batch.setTechnologyProcess(savedProcess);
                    savedProcess.setQrCodeUrl(generateQrCode(savedProcess));
                    technologyProcessRepository.save(savedProcess);

                    technologyProcesses.add(savedProcess);
                    System.err.println("Đã tạo TechnologyProcess cho mẻ " + (i + 1) + " (DyeBatch ID: " + batch.getId() + ")");
                }

                DyeBatch lastBatch = dyeBatches.get(totalBatches - 1);
                BigDecimal coneBatchWeightLast = lastBatch.getCone_batch_weight();
                if (coneBatchWeightLast == null) {
                    throw new IllegalStateException("cone_batch_weight của DyeBatch cuối không được null.");
                }
                System.err.println("cone_batch_weight của mẻ cuối: " + coneBatchWeightLast);
                ProcessParameters paramsLast = new ProcessParameters(coneBatchWeightLast);
                System.err.println("Thông số TechnologyProcess cho mẻ cuối: avcoLveDlxPlus=" + paramsLast.avcoLveDlxPlus + ", chelator=" + paramsLast.chelator + ", detergent=" + paramsLast.detergent +
                        ", reducingAgent=" + paramsLast.reducingAgent + ", dfm=" + paramsLast.dfm + ", axit=" + paramsLast.axit + ", anbatex=" + paramsLast.anbatex + ", liquorRatio=" + paramsLast.liquorRatio +
                        ", dispergatorN=" + dispergatorNForLastBatch);

                TechnologyProcess lastProcess = new TechnologyProcess();
                lastProcess.setCreatedBy(currentUser);
                lastProcess.setCreateAt(LocalDateTime.now());
                lastProcess.setUpdateAt(LocalDateTime.now());
                lastProcess.setAvcoLveDlxPlus(paramsLast.avcoLveDlxPlus);
                lastProcess.setChelator(paramsLast.chelator);
                lastProcess.setDetergent(paramsLast.detergent);
                lastProcess.setReducingAgent(paramsLast.reducingAgent);
                lastProcess.setDfm(paramsLast.dfm);
                lastProcess.setAxit(paramsLast.axit);
                lastProcess.setAnbatex(paramsLast.anbatex);
                lastProcess.setLiquorRatio(paramsLast.liquorRatio);
                lastProcess.setDispergatorN(dispergatorNForLastBatch);
                lastProcess.setDyeBatch(lastBatch);
                lastProcess.setSendStatus(SendEnum.NOT_SENT);
                lastProcess.setStatus(BaseEnum.DRAFT);
                lastProcess.setDyeTypes(new ArrayList<>());

                TechnologyProcess savedLastProcess = technologyProcessRepository.save(lastProcess);
                List<DyeType> dyeTypesForLastBatch = convertToDyeTypeEntities(dyeTypesForLastBatchDTO, savedLastProcess);
                savedLastProcess.setDyeTypes(dyeTypesForLastBatch);
                savedLastProcess = technologyProcessRepository.save(savedLastProcess);
                lastBatch.setTechnologyProcess(savedLastProcess);
                savedLastProcess.setQrCodeUrl(generateQrCode(savedLastProcess));
                technologyProcessRepository.save(savedLastProcess);

                technologyProcesses.add(savedLastProcess);
                System.err.println("Đã tạo TechnologyProcess cho mẻ cuối (DyeBatch ID: " + lastBatch.getId() + ")");
            } else {
                System.err.println("Trường hợp chỉ có 1 mẻ: Tính toán tự động dựa trên cone_batch_weight của mẻ đó.");
                DyeBatch singleBatch = dyeBatches.get(0);
                BigDecimal coneBatchWeight = singleBatch.getCone_batch_weight();
                if (coneBatchWeight == null) {
                    throw new IllegalStateException("cone_batch_weight của DyeBatch không được null.");
                }
                System.err.println("cone_batch_weight của mẻ duy nhất: " + coneBatchWeight);

                ProcessParameters params = new ProcessParameters(coneBatchWeight);
                System.err.println("Thông số TechnologyProcess cho mẻ duy nhất: avcoLveDlxPlus=" + params.avcoLveDlxPlus + ", chelator=" + params.chelator + ", detergent=" + params.detergent +
                        ", reducingAgent=" + params.reducingAgent + ", dfm=" + params.dfm + ", axit=" + params.axit + ", anbatex=" + params.anbatex + ", liquorRatio=" + params.liquorRatio +
                        ", dispergatorN=" + dispergatorNForFirstBatches);

                TechnologyProcess process = new TechnologyProcess();
                process.setCreatedBy(currentUser);
                process.setCreateAt(LocalDateTime.now());
                process.setUpdateAt(LocalDateTime.now());
                process.setAvcoLveDlxPlus(params.avcoLveDlxPlus);
                process.setChelator(params.chelator);
                process.setDetergent(params.detergent);
                process.setReducingAgent(params.reducingAgent);
                process.setDfm(params.dfm);
                process.setAxit(params.axit);
                process.setAnbatex(params.anbatex);
                process.setLiquorRatio(params.liquorRatio);
                process.setDispergatorN(dispergatorNForFirstBatches);
                process.setDyeBatch(singleBatch);
                process.setSendStatus(SendEnum.NOT_SENT);
                process.setStatus(BaseEnum.DRAFT);
                process.setDyeTypes(new ArrayList<>());

                TechnologyProcess savedProcess = technologyProcessRepository.save(process);
                List<DyeType> dyeTypesForFirstBatches = convertToDyeTypeEntities(dyeTypesForFirstBatchesDTO, savedProcess);
                savedProcess.setDyeTypes(dyeTypesForFirstBatches);
                savedProcess = technologyProcessRepository.save(savedProcess);
                singleBatch.setTechnologyProcess(savedProcess);
                savedProcess.setQrCodeUrl(generateQrCode(savedProcess));
                technologyProcessRepository.save(savedProcess);

                technologyProcesses.add(savedProcess);
                System.err.println("Đã tạo TechnologyProcess cho mẻ duy nhất (DyeBatch ID: " + singleBatch.getId() + ")");
            }

            System.err.println("Đã tạo thành công " + technologyProcesses.size() + " TechnologyProcess cho WorkOrderDetail ID: " + workOrderDetailId);
            return technologyProcesses;

        } catch (Exception e) {
            System.err.println("Lỗi khi tạo TechnologyProcess cho WorkOrderDetail ID: " + workOrderDetailId + " trong WorkOrder ID: " + workOrderId + " - " + e.getMessage());
            throw e;
        }


    }

    @Transactional
    @Override
    public List<TechnologyProcess> updateTechnologyProcess(Long workOrderId,
                                                           Long workOrderDetailId,
                                                           List<DyeTypeDTO> dyeTypesForFirstBatchesDTO,
                                                           List<DyeTypeDTO> dyeTypesForLastBatchDTO,
                                                           BigDecimal dispergatorNForFirstBatches,
                                                           BigDecimal dispergatorNForLastBatch,
                                                           User currentUser) {
        try {
            System.err.println("Bắt đầu cập nhật TechnologyProcess cho WorkOrder ID: " + workOrderId +
                    ", WorkOrderDetail ID: " + workOrderDetailId);

            WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy WorkOrder với ID: " + workOrderId));
            System.err.println("Đã tìm thấy WorkOrder ID: " + workOrderId);

            if (workOrder.getStatus() != BaseEnum.APPROVED) {
                throw new IllegalStateException("WorkOrder phải ở trạng thái APPROVED để cập nhật TechnologyProcess. " +
                        "Trạng thái hiện tại: " + workOrder.getStatus());
            }
            System.err.println("WorkOrder ID: " + workOrderId + " có trạng thái APPROVED, tiếp tục xử lý.");

            WorkOrderDetail workOrderDetail = workOrder.getWorkOrderDetails().stream()
                    .filter(detail -> detail.getId().equals(workOrderDetailId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy WorkOrderDetail với ID: " + workOrderDetailId + " trong WorkOrder ID: " + workOrderId));
            System.err.println("Đã tìm thấy WorkOrderDetail ID: " + workOrderDetailId);

            List<DyeBatch> dyeBatches = workOrderDetail.getDyeStage().getDyebatches();
            System.err.println("Số lượng DyeBatch trong WorkOrderDetail ID: " + workOrderDetailId + " là: " + dyeBatches.size());

            dyeBatches.sort(Comparator.comparingInt(DyeBatch::getBatchNumber));
            validateDyeBatches(dyeBatches, workOrderDetailId);
            System.err.println("Tất cả DyeBatch với batchNumber = 1 trong WorkOrderDetail ID: " + workOrderDetailId + " có trạng thái NOT_STARTED, tiếp tục xử lý.");

            int totalBatches = dyeBatches.size();
            System.err.println("Sắp xếp DyeBatch hoàn tất, tổng số mẻ: " + totalBatches);

            validateDyeTypes(dyeTypesForFirstBatchesDTO, dyeTypesForLastBatchDTO, totalBatches);
            System.err.println("Đã kiểm tra dyeTypes: dyeTypesForFirstBatches có " + dyeTypesForFirstBatchesDTO.size() + " phần tử, dyeTypesForLastBatch có " + (dyeTypesForLastBatchDTO != null ? dyeTypesForLastBatchDTO.size() : 0) + " phần tử.");

            List<TechnologyProcess> technologyProcesses = new ArrayList<>();

            if (totalBatches > 1) {
                System.err.println("Trường hợp có nhiều mẻ (" + totalBatches + " mẻ): các mẻ đầu giống nhau, mẻ cuối khác.");

                BigDecimal coneBatchWeightFirst = dyeBatches.get(0).getCone_batch_weight();
                if (coneBatchWeightFirst == null) {
                    throw new IllegalStateException("cone_batch_weight của DyeBatch đầu tiên không được null.");
                }
                System.err.println("cone_batch_weight của mẻ đầu tiên: " + coneBatchWeightFirst);

                ProcessParameters paramsFirst = new ProcessParameters(coneBatchWeightFirst);
                System.err.println("Thông số TechnologyProcess cho các mẻ đầu: avcoLveDlxPlus=" + paramsFirst.avcoLveDlxPlus + ", chelator=" + paramsFirst.chelator + ", detergent=" + paramsFirst.detergent +
                        ", reducingAgent=" + paramsFirst.reducingAgent + ", dfm=" + paramsFirst.dfm + ", axit=" + paramsFirst.axit + ", anbatex=" + paramsFirst.anbatex + ", liquorRatio=" + paramsFirst.liquorRatio +
                        ", dispergatorN=" + dispergatorNForFirstBatches);

                for (int i = 0; i < totalBatches - 1; i++) {
                    DyeBatch batch = dyeBatches.get(i);
                    TechnologyProcess processForBatch = batch.getTechnologyProcess();
                    if (processForBatch == null) {
                        processForBatch = new TechnologyProcess();
                        processForBatch.setCreatedBy(currentUser);
                        processForBatch.setCreateAt(LocalDateTime.now());
                        processForBatch.setDyeBatch(batch);
                        processForBatch.setSendStatus(SendEnum.NOT_SENT);
                        processForBatch.setStatus(BaseEnum.DRAFT);
                        processForBatch.setDyeTypes(new ArrayList<>());
                    } else {
                        if (!processForBatch.getCreatedBy().equals(currentUser)) {
                            throw new IllegalStateException("Chỉ người tạo TechnologyProcess mới có quyền cập nhật. Người tạo: " + processForBatch.getCreatedBy().getUsername());
                        }
                        if (processForBatch.getStatus() != BaseEnum.DRAFT) {
                            throw new IllegalStateException("Chỉ có thể cập nhật TechnologyProcess ở trạng thái DRAFT. Trạng thái hiện tại của TechnologyProcess ID: " + processForBatch.getId() + " là " + processForBatch.getStatus());
                        }
                        // Xóa hoàn toàn DyeTypes cũ trong database
                        dyeTypeRepository.deleteByTechnologyProcessId(processForBatch.getId());
                        processForBatch.setDyeTypes(new ArrayList<>());
                    }

                    processForBatch.setUpdateAt(LocalDateTime.now());
                    processForBatch.setAvcoLveDlxPlus(paramsFirst.avcoLveDlxPlus);
                    processForBatch.setChelator(paramsFirst.chelator);
                    processForBatch.setDetergent(paramsFirst.detergent);
                    processForBatch.setReducingAgent(paramsFirst.reducingAgent);
                    processForBatch.setDfm(paramsFirst.dfm);
                    processForBatch.setAxit(paramsFirst.axit);
                    processForBatch.setAnbatex(paramsFirst.anbatex);
                    processForBatch.setLiquorRatio(paramsFirst.liquorRatio);
                    processForBatch.setDispergatorN(dispergatorNForFirstBatches);

                    List<DyeType> dyeTypesForFirstBatches = convertToDyeTypeEntities(dyeTypesForFirstBatchesDTO, processForBatch);
                    processForBatch.setDyeTypes(dyeTypesForFirstBatches);

                    TechnologyProcess updatedProcess = technologyProcessRepository.save(processForBatch);
                    batch.setTechnologyProcess(updatedProcess);
                    updatedProcess.setQrCodeUrl(generateQrCode(updatedProcess));
                    technologyProcessRepository.save(updatedProcess);
                    technologyProcesses.add(updatedProcess);
                    System.err.println("Đã cập nhật/tạo mới TechnologyProcess cho mẻ " + (i + 1) + " (DyeBatch ID: " + batch.getId() + ")");
                }

                DyeBatch lastBatch = dyeBatches.get(totalBatches - 1);
                BigDecimal coneBatchWeightLast = lastBatch.getCone_batch_weight();
                if (coneBatchWeightLast == null) {
                    throw new IllegalStateException("cone_batch_weight của DyeBatch cuối không được null.");
                }
                System.err.println("cone_batch_weight của mẻ cuối: " + coneBatchWeightLast);

                ProcessParameters paramsLast = new ProcessParameters(coneBatchWeightLast);
                System.err.println("Thông số TechnologyProcess cho mẻ cuối: avcoLveDlxPlus=" + paramsLast.avcoLveDlxPlus + ", chelator=" + paramsLast.chelator + ", detergent=" + paramsLast.detergent +
                        ", reducingAgent=" + paramsLast.reducingAgent + ", dfm=" + paramsLast.dfm + ", axit=" + paramsLast.axit + ", anbatex=" + paramsLast.anbatex + ", liquorRatio=" + paramsLast.liquorRatio +
                        ", dispergatorN=" + dispergatorNForLastBatch);

                TechnologyProcess lastProcess = lastBatch.getTechnologyProcess();
                if (lastProcess == null) {
                    lastProcess = new TechnologyProcess();
                    lastProcess.setCreatedBy(currentUser);
                    lastProcess.setCreateAt(LocalDateTime.now());
                    lastProcess.setDyeBatch(lastBatch);
                    lastProcess.setSendStatus(SendEnum.NOT_SENT);
                    lastProcess.setStatus(BaseEnum.DRAFT);
                    lastProcess.setDyeTypes(new ArrayList<>());
                } else {
                    if (!lastProcess.getCreatedBy().equals(currentUser)) {
                        throw new IllegalStateException("Chỉ người tạo TechnologyProcess mới có quyền cập nhật. Người tạo: " + lastProcess.getCreatedBy().getUsername());
                    }
                    if (lastProcess.getStatus() != BaseEnum.DRAFT) {
                        throw new IllegalStateException("Chỉ có thể cập nhật TechnologyProcess ở trạng thái DRAFT. Trạng thái hiện tại của TechnologyProcess ID: " + lastProcess.getId() + " là " + lastProcess.getStatus());
                    }
                    // Xóa hoàn toàn DyeTypes cũ trong database
                    dyeTypeRepository.deleteByTechnologyProcessId(lastProcess.getId());
                    lastProcess.setDyeTypes(new ArrayList<>());
                }

                lastProcess.setUpdateAt(LocalDateTime.now());
                lastProcess.setAvcoLveDlxPlus(paramsLast.avcoLveDlxPlus);
                lastProcess.setChelator(paramsLast.chelator);
                lastProcess.setDetergent(paramsLast.detergent);
                lastProcess.setReducingAgent(paramsLast.reducingAgent);
                lastProcess.setDfm(paramsLast.dfm);
                lastProcess.setAxit(paramsLast.axit);
                lastProcess.setAnbatex(paramsLast.anbatex);
                lastProcess.setLiquorRatio(paramsLast.liquorRatio);
                lastProcess.setDispergatorN(dispergatorNForLastBatch);

                List<DyeType> dyeTypesForLastBatch = convertToDyeTypeEntities(dyeTypesForLastBatchDTO, lastProcess);
                lastProcess.setDyeTypes(dyeTypesForLastBatch);

                TechnologyProcess updatedLastProcess = technologyProcessRepository.save(lastProcess);
                lastBatch.setTechnologyProcess(updatedLastProcess);
                updatedLastProcess.setQrCodeUrl(generateQrCode(updatedLastProcess));
                technologyProcessRepository.save(updatedLastProcess);
                technologyProcesses.add(updatedLastProcess);
                System.err.println("Đã cập nhật/tạo mới TechnologyProcess cho mẻ cuối (DyeBatch ID: " + lastBatch.getId() + ")");
            } else {
                System.err.println("Trường hợp chỉ có 1 mẻ: Tính toán tự động dựa trên cone_batch_weight của mẻ đó.");

                DyeBatch singleBatch = dyeBatches.get(0);
                BigDecimal coneBatchWeight = singleBatch.getCone_batch_weight();
                if (coneBatchWeight == null) {
                    throw new IllegalStateException("cone_batch_weight của DyeBatch không được null.");
                }
                System.err.println("cone_batch_weight của mẻ duy nhất: " + coneBatchWeight);

                ProcessParameters params = new ProcessParameters(coneBatchWeight);
                System.err.println("Thông số TechnologyProcess cho mẻ duy nhất: avcoLveDlxPlus=" + params.avcoLveDlxPlus + ", chelator=" + params.chelator + ", detergent=" + params.detergent +
                        ", reducingAgent=" + params.reducingAgent + ", dfm=" + params.dfm + ", axit=" + params.axit + ", anbatex=" + params.anbatex + ", liquorRatio=" + params.liquorRatio +
                        ", dispergatorN=" + dispergatorNForFirstBatches);

                TechnologyProcess process = singleBatch.getTechnologyProcess();
                if (process == null) {
                    process = new TechnologyProcess();
                    process.setCreatedBy(currentUser);
                    process.setCreateAt(LocalDateTime.now());
                    process.setDyeBatch(singleBatch);
                    process.setSendStatus(SendEnum.NOT_SENT);
                    process.setStatus(BaseEnum.DRAFT);
                    process.setDyeTypes(new ArrayList<>());
                } else {
                    if (!process.getCreatedBy().equals(currentUser)) {
                        throw new IllegalStateException("Chỉ người tạo TechnologyProcess mới có quyền cập nhật. Người tạo: " + process.getCreatedBy().getUsername());
                    }
                    if (process.getStatus() != BaseEnum.DRAFT) {
                        throw new IllegalStateException("Chỉ có thể cập nhật TechnologyProcess ở trạng thái DRAFT. Trạng thái hiện tại của TechnologyProcess ID: " + process.getId() + " là " + process.getStatus());
                    }
                    // Xóa hoàn toàn DyeTypes cũ trong database
                    dyeTypeRepository.deleteByTechnologyProcessId(process.getId());
                    process.setDyeTypes(new ArrayList<>());
                }

                process.setUpdateAt(LocalDateTime.now());
                process.setAvcoLveDlxPlus(params.avcoLveDlxPlus);
                process.setChelator(params.chelator);
                process.setDetergent(params.detergent);
                process.setReducingAgent(params.reducingAgent);
                process.setDfm(params.dfm);
                process.setAxit(params.axit);
                process.setAnbatex(params.anbatex);
                process.setLiquorRatio(params.liquorRatio);
                process.setDispergatorN(dispergatorNForFirstBatches);

                List<DyeType> dyeTypesForFirstBatches = convertToDyeTypeEntities(dyeTypesForFirstBatchesDTO, process);
                process.setDyeTypes(dyeTypesForFirstBatches);

                TechnologyProcess updatedProcess = technologyProcessRepository.save(process);
                singleBatch.setTechnologyProcess(updatedProcess);
                updatedProcess.setQrCodeUrl(generateQrCode(updatedProcess));
                technologyProcessRepository.save(updatedProcess);
                technologyProcesses.add(updatedProcess);
                System.err.println("Đã cập nhật/tạo mới TechnologyProcess cho mẻ duy nhất (DyeBatch ID: " + singleBatch.getId() + ")");
            }

            System.err.println("Đã cập nhật thành công " + technologyProcesses.size() + " TechnologyProcess cho WorkOrderDetail ID: " + workOrderDetailId);
            return technologyProcesses;

        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật TechnologyProcess cho WorkOrderDetail ID: " + workOrderDetailId + " trong WorkOrder ID: " + workOrderId + " - " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    @Override
    public void submitTechnologyProcesses(Long workOrderId) {
        try {
            System.err.println("Bắt đầu submit TechnologyProcess cho WorkOrder ID: " + workOrderId);

            WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy WorkOrder với ID: " + workOrderId));
            System.err.println("Đã tìm thấy WorkOrder ID: " + workOrderId);

            if (workOrder.getStatus() != BaseEnum.APPROVED) {
                throw new IllegalStateException("WorkOrder phải ở trạng thái APPROVED để submit TechnologyProcess. Trạng thái hiện tại: " + workOrder.getStatus());
            }
            System.err.println("WorkOrder ID: " + workOrderId + " có trạng thái APPROVED, tiếp tục xử lý.");

            List<WorkOrderDetail> workOrderDetails = workOrder.getWorkOrderDetails();
            if (workOrderDetails == null || workOrderDetails.isEmpty()) {
                throw new IllegalStateException("WorkOrder ID: " + workOrderId + " không có WorkOrderDetails.");
            }
            System.err.println("WorkOrder ID: " + workOrderId + " có " + workOrderDetails.size() + " WorkOrderDetails.");

            for (WorkOrderDetail detail : workOrderDetails) {
                List<DyeBatch> dyeBatches = detail.getDyeStage().getDyebatches();
                if (dyeBatches == null || dyeBatches.isEmpty()) {
                    throw new IllegalStateException("WorkOrderDetail ID: " + detail.getId() + " không có DyeBatch.");
                }

                for (DyeBatch batch : dyeBatches) {
                    TechnologyProcess technologyProcess = batch.getTechnologyProcess();
                    if (technologyProcess == null) {
                        throw new IllegalStateException("DyeBatch ID: " + batch.getId() + " trong WorkOrderDetail ID: " + detail.getId() + " chưa có TechnologyProcess.");
                    }

                    List<DyeType> dyeTypes = technologyProcess.getDyeTypes();
                    if (dyeTypes == null || dyeTypes.isEmpty()) {
                        throw new IllegalStateException("TechnologyProcess ID: " + technologyProcess.getId() + " của DyeBatch ID: " + batch.getId() + " không có DyeTypes.");
                    }
                }
            }
            System.err.println("Tất cả DyeBatch trong WorkOrder ID: " + workOrderId + " đều có TechnologyProcess và DyeTypes, tiếp tục xử lý.");

            for (WorkOrderDetail detail : workOrderDetails) {
                for (DyeBatch batch : detail.getDyeStage().getDyebatches()) {
                    TechnologyProcess technologyProcess = batch.getTechnologyProcess();
                    technologyProcess.setStatus(BaseEnum.APPROVED);
                    technologyProcess.setSendStatus(SendEnum.SENT);
                    technologyProcess.setUpdateAt(LocalDateTime.now());
                    technologyProcessRepository.save(technologyProcess);
                    System.err.println("Đã cập nhật trạng thái TechnologyProcess ID: " + technologyProcess.getId() + " sang APPROVED.");
                }
            }

            System.err.println("Đã submit thành công TechnologyProcess cho WorkOrder ID: " + workOrderId);

        } catch (Exception e) {
            System.err.println("Lỗi khi submit TechnologyProcess cho WorkOrder ID: " + workOrderId + " - " + e.getMessage());
            throw e;
        }
    }

    @Override
    public TechnologyProcess getByDyeId(Long dyeId) {
        TechnologyProcess technologyProcess = technologyProcessRepository.getTechnologyProcessByBatchId(dyeId);
        return technologyProcess;
    }

    @Override
    public Page<TechnologyProcess> getAllTechnologyProcessesByCreatedBy(Pageable pageable, User createdBy) {
        System.err.println("Lấy tất cả TechnologyProcess do user " + createdBy.getUsername() + " tạo, page: " + pageable.getPageNumber() + ", size: " + pageable.getPageSize());
        return technologyProcessRepository.findByCreatedBy(createdBy, pageable);
    }

    @Override
    public Page<TechnologyProcess> getTechnologyProcessesByStatusAndCreatedBy(BaseEnum status, Pageable pageable, User createdBy) {
        System.err.println("Lấy TechnologyProcess với trạng thái " + status + " do user " + createdBy.getUsername() + " tạo, page: " + pageable.getPageNumber() + ", size: " + pageable.getPageSize());
        return technologyProcessRepository.findByStatusAndCreatedBy(status, createdBy, pageable);
    }

    @Override
    public TechnologyProcess getTechnologyProcessByIdAndCreatedBy(Long id, User createdBy) {
        System.err.println("Tìm TechnologyProcess với ID: " + id + " do user " + createdBy.getUsername() + " tạo");
        return technologyProcessRepository.findByIdAndCreatedBy(id, createdBy)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy TechnologyProcess với ID: " + id + " do user: " + createdBy.getUsername() + " tạo"));
    }
}