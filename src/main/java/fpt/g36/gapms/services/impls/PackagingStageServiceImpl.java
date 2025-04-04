package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.repositories.*;
import fpt.g36.gapms.services.ImageService;
import fpt.g36.gapms.services.PackagingStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PackagingStageServiceImpl implements PackagingStageService {
    @Autowired
    private PackagingStageRepository packagingStageRepository;

    @Autowired
    private PackagingRiskAssessmentRepository packagingRiskAssessmentRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PackagingBatchRepository packagingBatchRepository;

    @Autowired
    private WorkOrderDetailsRepository workOrderDetailsRepository;

    @Autowired
    private PhotoStageRepository photoStageRepository;

    @Override
    public List<PackagingStage> getAllPackagingStageForPackagingLead(Long woId) {
        List<PackagingStage> windingStages = packagingStageRepository.getAllPackagingStageByWorkOrderId(woId);
        return windingStages;
    }


    @Override
    public PackagingStage getPackagingStageById(Long psId) {
        PackagingStage packagingStage = packagingStageRepository.findById(psId).orElseThrow(() -> new RuntimeException("psId not found"));
        return packagingStage;
    }

    @Override
    public PackagingRiskAssessment getPackagingRiskAssessmentByPackagingBatchId(Long pbId) {
        List<PackagingRiskAssessment> packagingRiskAssessments = packagingRiskAssessmentRepository.getByPackagingBatchId(pbId);
        if (packagingRiskAssessments.isEmpty()) {
            PackagingRiskAssessment packagingRiskAssessment = null;
            return packagingRiskAssessment; // Hoặc ném một ngoại lệ tùy chỉnh nếu cần
        }
        PackagingRiskAssessment packagingRiskAssessment = packagingRiskAssessments.get(0);
        return packagingRiskAssessment;
    }

    @Override
    public PackagingRiskAssessment saveTestPackaging(Long id, PackagingRiskAssessment packagingRiskAssessment, User qaPackaging, MultipartFile[] photos) throws IOException {
        PackagingRiskAssessment packagingRiskAssessment_save = packagingRiskAssessmentRepository.findById(id).orElseThrow(() -> new RuntimeException("praId not found"));
        packagingRiskAssessment_save.setFirstStamp(packagingRiskAssessment.getFirstStamp());
        packagingRiskAssessment_save.setCoreStamp(packagingRiskAssessment.getCoreStamp());
        packagingRiskAssessment_save.setDozenStamp(packagingRiskAssessment.getDozenStamp());
        packagingRiskAssessment_save.setKcsStamp(packagingRiskAssessment.getKcsStamp());
        packagingRiskAssessment_save.setPass(packagingRiskAssessment.getPass());
        packagingRiskAssessment_save.setCreateBy(qaPackaging);
        if(packagingRiskAssessment.getPass()!=null){
            packagingRiskAssessment_save.getPackagingBatch().setTestStatus(TestEnum.TESTED);
            if(packagingRiskAssessment.getPass()) {
                packagingRiskAssessment_save.getPackagingBatch().setPass(true);
            }else {
                packagingRiskAssessment_save.getPackagingBatch().setPass(false);

            }
            List<PackagingBatch> packagingBatches = packagingBatchRepository.getAllWindingBatchByPackagingStageId(packagingRiskAssessment.getPackagingBatch().getPackagingStage().getId());
            boolean allTested = packagingBatches.stream()
                    .allMatch(packagingBatch -> packagingBatch.getTestStatus() == TestEnum.TESTED && packagingBatch.getPass());
            if (allTested) {
                packagingRiskAssessment_save.getPackagingBatch().getPackagingStage().setWorkStatus(WorkEnum.FINISHED);
                packagingRiskAssessment_save.getPackagingBatch().getPackagingStage().getWorkOrderDetail().setWorkStatus(WorkEnum.FINISHED);
            }

            List<WorkOrderDetail> workOrderDetails = workOrderDetailsRepository.getAllByWorkOrderId(packagingRiskAssessment.getPackagingBatch().getPackagingStage().getWorkOrderDetail().getWorkOrder().getId());
            boolean allWorkOrderDetailStatus = workOrderDetails.stream()
                    .allMatch(workOrderDetail -> workOrderDetail.getWorkStatus() == WorkEnum.FINISHED);
            if (allWorkOrderDetailStatus) {
                packagingRiskAssessment_save.getPackagingBatch().getPackagingStage().getWorkOrderDetail().getWorkOrder().setIsProduction(WorkEnum.FINISHED);
            }
        }
        packagingRiskAssessmentRepository.save(packagingRiskAssessment_save);
        List<PhotoStage> photoStages = new ArrayList<>();
        List<String> images = imageService.saveListImageMultiFile(photos);
        for (String photoUrl : images) {
            PhotoStage photoStage = new PhotoStage();
            photoStage.setPhoto(photoUrl);
            photoStage.setPackagingRiskAssessment(packagingRiskAssessment_save);// Lưu từng ảnh riêng biệt
            photoStages.add(photoStage);
        }
        photoStageRepository.saveAll(photoStages);

        return packagingRiskAssessment_save;
    }
}
