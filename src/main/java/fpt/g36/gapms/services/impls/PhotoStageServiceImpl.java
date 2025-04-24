package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.PhotoStage;
import fpt.g36.gapms.repositories.PhotoStageRepository;
import fpt.g36.gapms.services.PhotoStageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoStageServiceImpl implements PhotoStageService {

    @Autowired
    private PhotoStageRepository photoStageRepository;

    @Value("${file.upload-dir:./uploads/}") // Đường dẫn đến thư mục uploads
    private String uploadDir;

    @Override
    public List<PhotoStage> getAllPhotoStageByDraId(Long draId) {
        List<PhotoStage> photoStages = photoStageRepository.getAllPhotoStageByDyeRiskId(draId);
        return photoStages;
    }

    @Override
    public List<PhotoStage> getAllPhotoStageByWraId(Long wraId) {
        List<PhotoStage> photoStages = photoStageRepository.getAllPhotoStageByWindingRiskId(wraId);
        return photoStages;
    }

    @Override
    public List<PhotoStage> getAllPhotoStageByPraId(Long praId) {
        List<PhotoStage> photoStages = photoStageRepository.getAllPhotoStageByPackagingRiskId(praId);
        return photoStages;
    }

    @Transactional
    public boolean deletePhoto(String photoName) {
        Optional<PhotoStage> photoOptional = photoStageRepository.findByPhoto(photoName);
        if (photoOptional.isPresent()) {
            // Xóa bản ghi trong database
            photoStageRepository.delete(photoOptional.get());

            // Xóa file ảnh trong thư mục uploads
            File file = new File(uploadDir + photoName);
            if (file.exists()) {
                return file.delete();
            }
            return true; // Nếu file không tồn tại, vẫn coi là thành công
        }
        return false;
    }
}
