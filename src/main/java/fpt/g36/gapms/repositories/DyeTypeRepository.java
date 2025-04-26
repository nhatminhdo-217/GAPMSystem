package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.DyeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DyeTypeRepository extends JpaRepository<DyeType, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM DyeType dt WHERE dt.technologyProcess.id = :technologyProcessId")
    void deleteByTechnologyProcessId(Long technologyProcessId);
}
