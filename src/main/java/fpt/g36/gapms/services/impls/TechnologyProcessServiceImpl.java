package fpt.g36.gapms.services.impls;


import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.*;
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


@Service
public class TechnologyProcessServiceImpl implements TechnologyProcessService {

    @Autowired
    private TechnologyProcessRepository technologyProcessRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Override
    public TechnologyProcess getTechnologyProcessByIdAndCreatedBy(Long id, User createdBy) {
        return technologyProcessRepository.findByIdAndCreatedBy(id, createdBy)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy TechnologyProcess với ID: " + id + " và createdBy: " + createdBy.getUsername()));
    }

    @Override
    public Page<TechnologyProcess> getAllTechnologyProcessesByCreatedBy(Pageable pageable, User createdBy) {
        System.err.println("Lấy danh sách TechnologyProcess của user: " + createdBy.getUsername() + ", sắp xếp theo updateAt DESC");
        return technologyProcessRepository.findByCreatedBy(createdBy, pageable);
    }

    @Override
    public Page<TechnologyProcess> getTechnicalProcessByStatusAndCreatedBy(SendEnum status, Pageable pageable, User createdBy) {
        System.err.println("Lấy danh sách TechnologyProcess với status: " + status + " của user: " + createdBy.getUsername() + ", sắp xếp theo updateAt DESC");
        return technologyProcessRepository.findByStatusAndCreatedBy(status, createdBy, pageable);
    }

    @Override
    @Transactional
    public List<TechnologyProcess> createTechnologyProcess(Long workOrderId, Long workOrderDetailId, List<DyeType> dyeTypesForFirstBatches, List<DyeType> dyeTypesForLastBatch, BigDecimal dispergatorNForFirstBatches, BigDecimal dispergatorNForLastBatch) {
        try {
            System.err.println("Bắt đầu tạo TechnologyProcess cho WorkOrder ID: " + workOrderId + ", WorkOrderDetail ID: " + workOrderDetailId);

            // 1. Tìm WorkOrder
            WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy WorkOrder với ID: " + workOrderId));
            System.err.println("Đã tìm thấy WorkOrder ID: " + workOrderId);

            // 2. Kiểm tra trạng thái WorkOrder
            if (workOrder.getStatus() != BaseEnum.APPROVED) {
                throw new IllegalStateException("WorkOrder phải ở trạng thái APPROVED để tạo TechnologyProcess. Trạng thái hiện tại: " + workOrder.getStatus());
            }
            System.err.println("WorkOrder ID: " + workOrderId + " có trạng thái APPROVED, tiếp tục xử lý.");

            // 3. Tìm WorkOrderDetail
            WorkOrderDetail workOrderDetail = workOrder.getWorkOrderDetails().stream()
                    .filter(detail -> detail.getId().equals(workOrderDetailId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy WorkOrderDetail với ID: " + workOrderDetailId + " trong WorkOrder ID: " + workOrderId));
            System.err.println("Đã tìm thấy WorkOrderDetail ID: " + workOrderDetailId);

            // 4. Lấy danh sách DyeBatch từ WorkOrderDetail
            List<DyeBatch> dyeBatches = workOrderDetail.getDyeStage().getDyebatches();
            System.err.println("Số lượng DyeBatch trong WorkOrderDetail ID: " + workOrderDetailId + " là: " + dyeBatches.size());

            // 5. Kiểm tra workStatus của DyeBatch có batchNumber = 1
            boolean isBatchOneNotStarted = dyeBatches.stream()
                    .filter(batch -> batch.getBatchNumber() == 1)
                    .allMatch(batch -> batch.getWorkStatus() == WorkEnum.NOT_STARTED);

            if (!isBatchOneNotStarted) {
                throw new IllegalStateException("Tất cả DyeBatch với batchNumber = 1 trong WorkOrderDetail ID: " + workOrderDetailId + " phải ở trạng thái NOT_STARTED để tạo TechnologyProcess.");
            }
            System.err.println("Tất cả DyeBatch với batchNumber = 1 trong WorkOrderDetail ID: " + workOrderDetailId + " có trạng thái NOT_STARTED, tiếp tục xử lý.");

            // 6. Sắp xếp DyeBatch theo batchNumber
            dyeBatches.sort((b1, b2) -> Integer.compare(b1.getBatchNumber(), b2.getBatchNumber()));
            int totalBatches = dyeBatches.size();
            System.err.println("Sắp xếp DyeBatch hoàn tất, tổng số mẻ: " + totalBatches);

            if (totalBatches < 1) {
                throw new IllegalStateException("WorkOrderDetail ID: " + workOrderDetailId + " không có DyeBatch để tạo TechnologyProcess.");
            }

            // 7. Kiểm tra dyeTypes không rỗng
            if (dyeTypesForFirstBatches == null || dyeTypesForFirstBatches.isEmpty()) {
                throw new IllegalArgumentException("dyeTypesForFirstBatches không được rỗng.");
            }
            if (totalBatches > 1 && (dyeTypesForLastBatch == null || dyeTypesForLastBatch.isEmpty())) {
                throw new IllegalArgumentException("dyeTypesForLastBatch không được rỗng khi có nhiều hơn 1 mẻ.");
            }
            System.err.println("Đã kiểm tra dyeTypes: dyeTypesForFirstBatches có " + dyeTypesForFirstBatches.size() + " phần tử, dyeTypesForLastBatch có " + (dyeTypesForLastBatch != null ? dyeTypesForLastBatch.size() : 0) + " phần tử.");

            // 8. Tạo danh sách TechnologyProcess
            List<TechnologyProcess> technologyProcesses = new ArrayList<>();

            // 9. Xử lý các mẻ trong WorkOrderDetail
            if (totalBatches > 1) {
                System.err.println("Trường hợp có nhiều mẻ (" + totalBatches + " mẻ): các mẻ đầu giống nhau, mẻ cuối khác.");

                // Lấy cone_batch_weight từ mẻ đầu tiên để tính toán cho các mẻ đầu
                BigDecimal coneBatchWeightFirst = dyeBatches.get(0).getCone_batch_weight();
                if (coneBatchWeightFirst == null) {
                    throw new IllegalStateException("cone_batch_weight của DyeBatch đầu tiên không được null.");
                }
                System.err.println("cone_batch_weight của mẻ đầu tiên: " + coneBatchWeightFirst);

                // Tính toán các giá trị cho TechnologyProcess của các mẻ đầu
                BigDecimal avcoLveDlxPlusFirst = coneBatchWeightFirst.multiply(BigDecimal.valueOf(0.012)); // 1.2% * cone_batch_weight
                BigDecimal chelatorFirst = coneBatchWeightFirst.divide(BigDecimal.valueOf(2.5), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 2.5
                BigDecimal detergentFirst = coneBatchWeightFirst.divide(BigDecimal.valueOf(1.2), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 1.2
                BigDecimal reducingAgentFirst = coneBatchWeightFirst.divide(BigDecimal.valueOf(0.8), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 0.8
                BigDecimal dfmFirst = coneBatchWeightFirst.multiply(BigDecimal.valueOf(0.012)); // 1.2% * cone_batch_weight
                BigDecimal axitFirst = coneBatchWeightFirst.divide(BigDecimal.valueOf(1.2), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 1.2
                BigDecimal anbatexFirst = coneBatchWeightFirst.divide(BigDecimal.valueOf(1.5), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 1.5
                BigDecimal liquorRatio = BigDecimal.valueOf(6); // 1 kg vải cần 6 lít nước

                System.err.println("Thông số TechnologyProcess cho các mẻ đầu: avcoLveDlxPlus=" + avcoLveDlxPlusFirst + ", chelator=" + chelatorFirst + ", detergent=" + detergentFirst +
                        ", reducingAgent=" + reducingAgentFirst + ", dfm=" + dfmFirst + ", axit=" + axitFirst + ", anbatex=" + anbatexFirst + ", liquorRatio=" + liquorRatio +
                        ", dispergatorN=" + dispergatorNForFirstBatches);

                // Tạo TechnologyProcess chung cho các mẻ đầu (từ mẻ 1 đến mẻ totalBatches - 1)
                TechnologyProcess sharedProcess = new TechnologyProcess();
                sharedProcess.setCreatedBy(workOrder.getCreatedBy());
                sharedProcess.setCreateAt(LocalDateTime.now());
                sharedProcess.setUpdateAt(LocalDateTime.now());
                sharedProcess.setAvcoLveDlxPlus(avcoLveDlxPlusFirst);
                sharedProcess.setChelator(chelatorFirst);
                sharedProcess.setDetergent(detergentFirst);
                sharedProcess.setReducingAgent(reducingAgentFirst);
                sharedProcess.setDfm(dfmFirst);
                sharedProcess.setAxit(axitFirst);
                sharedProcess.setAnbatex(anbatexFirst);
                sharedProcess.setLiquorRatio(liquorRatio);
                sharedProcess.setDispergatorN(dispergatorNForFirstBatches); // Gán giá trị do người dùng nhập cho các mẻ đầu
                sharedProcess.setDyeTypes(dyeTypesForFirstBatches);

                // Gán TechnologyProcess cho các mẻ đầu
                for (int i = 0; i < totalBatches - 1; i++) {
                    DyeBatch batch = dyeBatches.get(i);
                    TechnologyProcess processForBatch = new TechnologyProcess();
                    processForBatch.setCreatedBy(sharedProcess.getCreatedBy());
                    processForBatch.setCreateAt(sharedProcess.getCreateAt());
                    processForBatch.setUpdateAt(sharedProcess.getUpdateAt());
                    processForBatch.setAvcoLveDlxPlus(sharedProcess.getAvcoLveDlxPlus());
                    processForBatch.setChelator(sharedProcess.getChelator());
                    processForBatch.setDetergent(sharedProcess.getDetergent());
                    processForBatch.setReducingAgent(sharedProcess.getReducingAgent());
                    processForBatch.setDfm(sharedProcess.getDfm());
                    processForBatch.setAxit(sharedProcess.getAxit());
                    processForBatch.setAnbatex(sharedProcess.getAnbatex());
                    processForBatch.setLiquorRatio(sharedProcess.getLiquorRatio());
                    processForBatch.setDispergatorN(sharedProcess.getDispergatorN());
                    processForBatch.setDyeTypes(sharedProcess.getDyeTypes());
                    processForBatch.setDyeBatch(batch);

                    // Lưu TechnologyProcess
                    technologyProcessRepository.save(processForBatch);
                    batch.setTechnologyProcess(processForBatch);
                    technologyProcesses.add(processForBatch);
                    System.err.println("Đã tạo TechnologyProcess cho mẻ " + (i + 1) + " (DyeBatch ID: " + batch.getId() + ")");
                }

                // Mẻ cuối: Tính toán tự động dựa trên cone_batch_weight của chính nó
                DyeBatch lastBatch = dyeBatches.get(totalBatches - 1);
                BigDecimal coneBatchWeightLast = lastBatch.getCone_batch_weight();
                if (coneBatchWeightLast == null) {
                    throw new IllegalStateException("cone_batch_weight của DyeBatch cuối không được null.");
                }
                System.err.println("cone_batch_weight của mẻ cuối: " + coneBatchWeightLast);

                // Tính toán các giá trị cho TechnologyProcess của mẻ cuối
                BigDecimal avcoLveDlxPlusLast = coneBatchWeightLast.multiply(BigDecimal.valueOf(0.012)); // 1.2% * cone_batch_weight
                BigDecimal chelatorLast = coneBatchWeightLast.divide(BigDecimal.valueOf(2.5), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 2.5
                BigDecimal detergentLast = coneBatchWeightLast.divide(BigDecimal.valueOf(1.2), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 1.2
                BigDecimal reducingAgentLast = coneBatchWeightLast.divide(BigDecimal.valueOf(0.8), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 0.8
                BigDecimal dfmLast = coneBatchWeightLast.multiply(BigDecimal.valueOf(0.012)); // 1.2% * cone_batch_weight
                BigDecimal axitLast = coneBatchWeightLast.divide(BigDecimal.valueOf(1.2), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 1.2
                BigDecimal anbatexLast = coneBatchWeightLast.divide(BigDecimal.valueOf(1.5), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 1.5

                System.err.println("Thông số TechnologyProcess cho mẻ cuối: avcoLveDlxPlus=" + avcoLveDlxPlusLast + ", chelator=" + chelatorLast + ", detergent=" + detergentLast +
                        ", reducingAgent=" + reducingAgentLast + ", dfm=" + dfmLast + ", axit=" + axitLast + ", anbatex=" + anbatexLast + ", liquorRatio=" + liquorRatio +
                        ", dispergatorN=" + dispergatorNForLastBatch);

                // Tạo TechnologyProcess cho mẻ cuối
                TechnologyProcess lastProcess = new TechnologyProcess();
                lastProcess.setCreatedBy(workOrder.getCreatedBy());
                lastProcess.setCreateAt(LocalDateTime.now());
                lastProcess.setUpdateAt(LocalDateTime.now());
                lastProcess.setAvcoLveDlxPlus(avcoLveDlxPlusLast);
                lastProcess.setChelator(chelatorLast);
                lastProcess.setDetergent(detergentLast);
                lastProcess.setReducingAgent(reducingAgentLast);
                lastProcess.setDfm(dfmLast);
                lastProcess.setAxit(axitLast);
                lastProcess.setAnbatex(anbatexLast);
                lastProcess.setLiquorRatio(liquorRatio);
                lastProcess.setDispergatorN(dispergatorNForLastBatch); // Gán giá trị do người dùng nhập cho mẻ cuối
                lastProcess.setDyeTypes(dyeTypesForLastBatch);
                lastProcess.setDyeBatch(lastBatch);

                // Lưu TechnologyProcess cho mẻ cuối
                technologyProcessRepository.save(lastProcess);
                lastBatch.setTechnologyProcess(lastProcess);
                technologyProcesses.add(lastProcess);
                System.err.println("Đã tạo TechnologyProcess cho mẻ cuối (DyeBatch ID: " + lastBatch.getId() + ")");
            } else {
                System.err.println("Trường hợp chỉ có 1 mẻ: Tính toán tự động dựa trên cone_batch_weight của mẻ đó.");

                // Trường hợp chỉ có 1 mẻ: Tính toán tự động dựa trên cone_batch_weight của mẻ đó
                DyeBatch singleBatch = dyeBatches.get(0);
                BigDecimal coneBatchWeight = singleBatch.getCone_batch_weight();
                if (coneBatchWeight == null) {
                    throw new IllegalStateException("cone_batch_weight của DyeBatch không được null.");
                }
                System.err.println("cone_batch_weight của mẻ duy nhất: " + coneBatchWeight);

                // Tính toán các giá trị cho TechnologyProcess
                BigDecimal avcoLveDlxPlus = coneBatchWeight.multiply(BigDecimal.valueOf(0.012)); // 1.2% * cone_batch_weight
                BigDecimal chelator = coneBatchWeight.divide(BigDecimal.valueOf(2.5), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 2.5
                BigDecimal detergent = coneBatchWeight.divide(BigDecimal.valueOf(1.2), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 1.2
                BigDecimal reducingAgent = coneBatchWeight.divide(BigDecimal.valueOf(0.8), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 0.8
                BigDecimal dfm = coneBatchWeight.multiply(BigDecimal.valueOf(0.012)); // 1.2% * cone_batch_weight
                BigDecimal axit = coneBatchWeight.divide(BigDecimal.valueOf(1.2), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 1.2
                BigDecimal anbatex = coneBatchWeight.divide(BigDecimal.valueOf(1.5), 2, BigDecimal.ROUND_HALF_UP); // cone_batch_weight / 1.5
                BigDecimal liquorRatio = BigDecimal.valueOf(6); // 1 kg vải cần 6 lít nước

                System.err.println("Thông số TechnologyProcess cho mẻ duy nhất: avcoLveDlxPlus=" + avcoLveDlxPlus + ", chelator=" + chelator + ", detergent=" + detergent +
                        ", reducingAgent=" + reducingAgent + ", dfm=" + dfm + ", axit=" + axit + ", anbatex=" + anbatex + ", liquorRatio=" + liquorRatio +
                        ", dispergatorN=" + dispergatorNForFirstBatches);

                // Tạo TechnologyProcess cho mẻ duy nhất
                TechnologyProcess process = new TechnologyProcess();
                process.setCreatedBy(workOrder.getCreatedBy());
                process.setCreateAt(LocalDateTime.now());
                process.setUpdateAt(LocalDateTime.now());
                process.setAvcoLveDlxPlus(avcoLveDlxPlus);
                process.setChelator(chelator);
                process.setDetergent(detergent);
                process.setReducingAgent(reducingAgent);
                process.setDfm(dfm);
                process.setAxit(axit);
                process.setAnbatex(anbatex);
                process.setLiquorRatio(liquorRatio);
                process.setDispergatorN(dispergatorNForFirstBatches); // Gán giá trị do người dùng nhập cho mẻ duy nhất
                process.setDyeTypes(dyeTypesForFirstBatches);
                process.setDyeBatch(singleBatch);

                // Lưu TechnologyProcess
                technologyProcessRepository.save(process);
                singleBatch.setTechnologyProcess(process);
                technologyProcesses.add(process);
                System.err.println("Đã tạo TechnologyProcess cho mẻ duy nhất (DyeBatch ID: " + singleBatch.getId() + ")");
            }

            System.err.println("Đã tạo thành công " + technologyProcesses.size() + " TechnologyProcess cho WorkOrderDetail ID: " + workOrderDetailId);
            return technologyProcesses;

        } catch (Exception e) {
            System.err.println("Lỗi khi tạo TechnologyProcess cho WorkOrderDetail ID: " + workOrderDetailId + " trong WorkOrder ID: " + workOrderId + " - " + e.getMessage());
            throw e;
        }
    }

    @Override
    public TechnologyProcess getByDyeId(Long dyeId) {
        TechnologyProcess  technologyProcess = technologyProcessRepository.getTechnologyProcessByBatchId(dyeId);
        return technologyProcess;
    }
}
