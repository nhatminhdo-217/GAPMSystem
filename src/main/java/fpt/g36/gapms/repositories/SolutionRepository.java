package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.entities.Solution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findAllByCreateBy_Id(Long createById);

    @Query("SELECT s FROM Solution s " +
            "JOIN s.rfq r " +
            "WHERE s.isSent = :sentStatus " +
            "AND r.approvedBy.id = :approvedById")
    List<Solution> findAllSentAndApprovedByUserId(@Param("sentStatus") SendEnum sentStatus, @Param("approvedById") Long approvedById);

    @Query("SELECT s FROM Solution s " +
            "JOIN s.rfq r " +
            "WHERE s.isSent = :sentStatus " +
            "AND r.approvedBy.id = :approvedById " +
            "ORDER BY s.createAt DESC")
    Page<Solution> findAllSentAndApprovedByUserId(
            @Param("sentStatus") SendEnum sentStatus,
            @Param("approvedById") Long approvedById,
            Pageable pageable
    );

    @Query("SELECT s FROM Solution s WHERE s.isSent = :sentStatus ORDER BY s.updateAt DESC")
    Page<Solution> findAllByIsSent(@Param("sentStatus") SendEnum sentStatus, Pageable pageable);

    @Query("SELECT s FROM Solution s " +
            "JOIN s.rfq r " +
            "WHERE s.createBy.id = :createById " +
            "AND s.isSent = :sentStatus " +
            "ORDER BY r.deadlineSolution ASC")
    Page<Solution> findAllByCreateByIdAndIsSentOrderByRfqDeadline(
            @Param("createById") Long createById,
            @Param("sentStatus") SendEnum sentStatus,
            Pageable pageable
    );

    //
    @Query("SELECT s FROM Solution s " +
            "WHERE s.id = :id " +
            "AND s.createBy.id = :createById")
    Optional<Solution> findByIdAndCreateById(
            @Param("id") Long id,
            @Param("createById") Long createById
    );
}
