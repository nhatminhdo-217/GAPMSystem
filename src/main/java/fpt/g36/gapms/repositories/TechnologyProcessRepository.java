package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.TechnologyProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyProcessRepository extends JpaRepository<TechnologyProcess, Long> {

    @Query("select tp from TechnologyProcess tp where tp.dyeBatch.id = :dyeId")
    TechnologyProcess getTechnologyProcessByBatchId(@Param("dyeId") Long dyeId);
}
