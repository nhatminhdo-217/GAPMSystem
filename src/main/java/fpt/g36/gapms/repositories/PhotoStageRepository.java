package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.PhotoStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoStageRepository extends JpaRepository<PhotoStage, Long> {

      @Query("select ps from PhotoStage ps where ps.dyeRiskAssessment.id = :draId")
       List<PhotoStage> getAllPhotoStageByDyeRiskId(Long draId);

    @Query("select ps from PhotoStage ps where ps.windingRiskAssessment.id = :wraId")
    List<PhotoStage> getAllPhotoStageByWindingRiskId(Long wraId);

    @Query("select ps from PhotoStage ps where ps.packagingRiskAssessment.id = :praId")
    List<PhotoStage> getAllPhotoStageByPackagingRiskId(Long praId);
}
