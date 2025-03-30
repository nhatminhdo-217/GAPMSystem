package fpt.g36.gapms.services.impls;


import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.DyeRiskAssessment;
import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.PhotoStage;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.DyeRiskAssessmentRepository;
import fpt.g36.gapms.repositories.DyeStageRepository;
import fpt.g36.gapms.repositories.PhotoStageRepository;
import fpt.g36.gapms.services.DyeStageService;
import fpt.g36.gapms.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DyeStageServiceImpl implements DyeStageService {

    @Autowired
    private DyeStageRepository dyeStageRepository;
    @Autowired
    private PhotoStageRepository photoStageRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private DyeRiskAssessmentRepository dyeRiskAssessmentRepository;

    @Override
    public List<DyeStage> getAllDyeStageForDyeLead(Long woId) {
        List<DyeStage> dyeStages = dyeStageRepository.getAllDyeStageByWorkOrderId(woId);
        return dyeStages;
    }

    @Override
    public void changeStatusDyeStageInProcess(Long dyeId, User leader) {

        DyeStage dyeStage = dyeStageRepository.findById(dyeId).orElseThrow(() -> new RuntimeException("dyeId not found"));

            dyeStage.setWorkStatus(WorkEnum.IN_PROGRESS);
            dyeStage.getWorkOrderDetail().getWorkOrder().setIsProduction(WorkEnum.IN_PROGRESS);
            dyeStage.setTestStatus(TestEnum.TESTING);
            dyeStage.setLeaderStart(leader);
            dyeStageRepository.save(dyeStage);

    }

    @Override
    public void changeStatusDyeStageFinish(Long dyeId, String photo, User leader) {
        DyeStage dyeStage = dyeStageRepository.findById(dyeId).orElseThrow(() -> new RuntimeException("dyeId not found"));
        dyeStage.setWorkStatus(WorkEnum.FINISHED);
        dyeStage.setDyePhoto(photo);
        dyeStage.setLeaderEnd(leader);
        dyeStageRepository.save(dyeStage);

        DyeRiskAssessment dyeRiskAssessment = new DyeRiskAssessment();
        dyeRiskAssessment.setDyeStage(dyeStage);
        dyeRiskAssessmentRepository.save(dyeRiskAssessment);

    }

    @Override
    public DyeStage getDyeStageById(Long dyeId) {
        DyeStage dyeStage = dyeStageRepository.findById(dyeId).orElseThrow(() -> new RuntimeException("dyeId not found"));
        return dyeStage;
    }

    @Override
    public DyeRiskAssessment getDyeRiskAssessmentByDyeStageId(Long dyeId) {
        List<DyeRiskAssessment> dyeRiskAssessments = dyeRiskAssessmentRepository.getByDyeId(dyeId);

        DyeRiskAssessment dyeRiskAssessment = dyeRiskAssessments.get(0);
        return dyeRiskAssessment;
    }

    @Override
    public DyeRiskAssessment saveTestDye(Long id, DyeRiskAssessment dyeRiskAssessment, User qaDye, MultipartFile[] photos) throws IOException {

        DyeRiskAssessment dyeRiskAssessment_save = dyeRiskAssessmentRepository.findById(id).orElseThrow(() -> new RuntimeException("dyeId not found"));dyeRiskAssessment_save.setColorTrue(dyeRiskAssessment.getColorTrue());
        dyeRiskAssessment_save.setColorTrue(dyeRiskAssessment.getColorTrue());
        dyeRiskAssessment_save.setHumidity(dyeRiskAssessment.getHumidity());
        dyeRiskAssessment_save.setLightTrue(dyeRiskAssessment.getLightTrue());
        dyeRiskAssessment_save.setColorFading(dyeRiskAssessment.getColorFading());
        dyeRiskAssessment_save.setMedication(dyeRiskAssessment.getMedication());
        dyeRiskAssessment_save.setMedicineSafe(dyeRiskAssessment.getMedicineSafe());
        dyeRiskAssessment_save.setIndustrialCleaningStains(dyeRiskAssessment.getIndustrialCleaningStains());
        dyeRiskAssessment_save.setCreateBy(qaDye);
        dyeRiskAssessment_save.setPass(dyeRiskAssessment.getPass());
        dyeRiskAssessmentRepository.save(dyeRiskAssessment_save);
        List<PhotoStage> photoStages = new ArrayList<>();
        List<String> images = imageService.saveListImageMultiFile(photos);
        for (String photoUrl : images) {
            PhotoStage photoStage = new PhotoStage();
            photoStage.setPhoto(photoUrl);
            photoStage.setDyeRiskAssessment(dyeRiskAssessment_save);// Lưu từng ảnh riêng biệt
            photoStages.add(photoStage);
        }
        photoStageRepository.saveAll(photoStages);

        return dyeRiskAssessment_save;
    }
}
