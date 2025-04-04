package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.repositories.PhotoStageRepository;
import fpt.g36.gapms.repositories.WindingBatchRepository;
import fpt.g36.gapms.repositories.WindingRiskAssessmentRepository;
import fpt.g36.gapms.repositories.WindingStageRepository;
import fpt.g36.gapms.services.ImageService;
import fpt.g36.gapms.services.WindingStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WindingStageServiceImpl implements WindingStageService {
    @Autowired
    private WindingStageRepository windingStageRepository;
    @Autowired
    private WindingRiskAssessmentRepository windingRiskAssessmentRepository;
    @Autowired
    private ImageService imageService;

    @Autowired
    private WindingBatchRepository windingBatchRepository;

    @Autowired
    private PhotoStageRepository photoStageRepository;

    @Override
    public List<WindingStage> getAllWindingStageForWindingLead(Long woId) {
        List<WindingStage> windingStages = windingStageRepository.getAllWindingStageByWorkOrderId(woId);
        return windingStages;
    }

    @Override
    public WindingStage getWindingStageById(Long wdId) {
        WindingStage windingStage = windingStageRepository.findById(wdId).orElseThrow(() -> new RuntimeException("wdId not found"));
        return windingStage;
    }

    @Override
    public WindingRiskAssessment getWindingRiskAssessmentByWindingBatchId(Long wbId) {
        List<WindingRiskAssessment> windingRiskAssessments = windingRiskAssessmentRepository.getByWindingBatchId(wbId);
        if (windingRiskAssessments.isEmpty()) {
            WindingRiskAssessment windingRiskAssessment = null;
            return windingRiskAssessment; // Hoặc ném một ngoại lệ tùy chỉnh nếu cần
        }
        WindingRiskAssessment windingRiskAssessment = windingRiskAssessments.get(0);
        return windingRiskAssessment;
    }

    @Override
    public WindingRiskAssessment saveTestWingding(Long id, WindingRiskAssessment windingRiskAssessment, User qaWinding, MultipartFile[] photos) throws IOException {
        WindingRiskAssessment windingRiskAssessment_save = windingRiskAssessmentRepository.findById(id).orElseThrow(() -> new RuntimeException("wsaId not found"));
        windingRiskAssessment_save.setTrueCone(windingRiskAssessment.getTrueCone());
        windingRiskAssessment_save.setFalseCone(windingRiskAssessment.getFalseCone());
        windingRiskAssessment_save.setColorFading(windingRiskAssessment.getColorFading());
        windingRiskAssessment_save.setColorUniformity(windingRiskAssessment.getColorUniformity());
        windingRiskAssessment_save.setCreateBy(qaWinding);
        windingRiskAssessment_save.setPass(windingRiskAssessment.getPass());
        if(windingRiskAssessment.getPass()!=null){
            windingRiskAssessment_save.getWindingBatch().setTestStatus(TestEnum.TESTED);
            if(windingRiskAssessment.getPass()) {
                windingRiskAssessment_save.getWindingBatch().setPass(true);
            }else {
                windingRiskAssessment_save.getWindingBatch().setPass(false);

            }
            List<WindingBatch> windingBatches = windingBatchRepository.getAllWindingBatchByWindingStageId(windingRiskAssessment.getWindingBatch().getWindingStage().getId());
            boolean allTested = windingBatches.stream()
                    .allMatch(windingBatch -> windingBatch.getTestStatus() == TestEnum.TESTED && windingBatch.getPass());
            if (allTested) {
                windingRiskAssessment_save.getWindingBatch().getWindingStage().setWorkStatus(WorkEnum.FINISHED);
            }
        }
        windingRiskAssessmentRepository.save(windingRiskAssessment_save);
        List<PhotoStage> photoStages = new ArrayList<>();
        List<String> images = imageService.saveListImageMultiFile(photos);
        for (String photoUrl : images) {
            PhotoStage photoStage = new PhotoStage();
            photoStage.setPhoto(photoUrl);
            photoStage.setWindingRiskAssessment(windingRiskAssessment_save);// Lưu từng ảnh riêng biệt
            photoStages.add(photoStage);
        }
        photoStageRepository.saveAll(photoStages);

        return windingRiskAssessment_save;
    }
}
