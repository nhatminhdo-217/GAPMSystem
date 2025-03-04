package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.entities.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findAllByCreateBy_Id(Long createById);

    @Query("SELECT s FROM Solution s " +
            "JOIN s.rfq r " +
            "WHERE s.isSent = :sentStatus " +
            "AND r.approvedBy.id = :approvedById")
    List<Solution> findAllSentAndApprovedByUserId(@Param("sentStatus") SendEnum sentStatus, @Param("approvedById") Long approvedById);
}
