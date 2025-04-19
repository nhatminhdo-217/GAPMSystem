package fpt.g36.gapms.repositories;


import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.entities.TechnologyProcess;
import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnologyProcessRepository extends JpaRepository<TechnologyProcess, Long> {
    Optional<TechnologyProcess> findByIdAndCreatedBy(Long id, User createdBy);

    @Query("SELECT tp FROM TechnologyProcess tp WHERE tp.createdBy = :createdBy ORDER BY tp.updateAt DESC")
    Page<TechnologyProcess> findByCreatedBy(@Param("createdBy") User createdBy, Pageable pageable);

    @Query("SELECT tp FROM TechnologyProcess tp WHERE tp.sendStatus = :status AND tp.createdBy = :createdBy ORDER BY tp.updateAt DESC")
    Page<TechnologyProcess> findByStatusAndCreatedBy(@Param("status") SendEnum status, @Param("createdBy") User createdBy, Pageable pageable);

    @Query("select tp from TechnologyProcess tp where tp.dyeBatch.id = :dyeId")
    TechnologyProcess getTechnologyProcessByBatchId(@Param("dyeId") Long dyeId);

}
