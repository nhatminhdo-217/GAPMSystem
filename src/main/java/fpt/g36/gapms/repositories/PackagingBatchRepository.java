package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.PackagingBatch;
import fpt.g36.gapms.models.entities.WindingBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackagingBatchRepository extends JpaRepository<PackagingBatch, Long> {

    @Query("select pb from PackagingBatch pb where pb.packagingStage.id = :psId order by pb.id asc")
    List<PackagingBatch> getAllWindingBatchByPackagingStageId(Long psId);
}
