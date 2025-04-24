package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.PhotoStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoStageRepository extends JpaRepository<PhotoStage, Long> {

      @Query("select ps from PhotoStage ps where ps.dyeRiskAssessment.id = :draId")
       List<PhotoStage> getAllPhotoStageByDyeRiskId(Long draId);

    @Query("select ps from PhotoStage ps where ps.windingRiskAssessment.id = :wraId")
    List<PhotoStage> getAllPhotoStageByWindingRiskId(Long wraId);

    @Query("select ps from PhotoStage ps where ps.packagingRiskAssessment.id = :praId")
    List<PhotoStage> getAllPhotoStageByPackagingRiskId(Long praId);

    Optional<PhotoStage> findByPhoto(String photo);

    @Modifying
    @Query("DELETE FROM PhotoStage p WHERE p.packagingRiskAssessment.packagingBatch.id IN :packagingBatchIds")
    void deleteByPackagingBatchIds(@Param("packagingBatchIds") List<Long> packagingBatchIds);

    @Modifying
    @Query("DELETE FROM PhotoStage p WHERE p.windingRiskAssessment.windingBatch.id IN :windingBatchIds")
    void deleteByWindingBatchIds(@Param("windingBatchIds") List<Long> windingBatchIds);

    @Modifying
    @Query("DELETE FROM PhotoStage p WHERE p.dyeRiskAssessment.dyeBatch.id IN :dyeBatchIds")
    void deleteByDyeBatchIds(@Param("dyeBatchIds") List<Long> dyeBatchIds);
}
