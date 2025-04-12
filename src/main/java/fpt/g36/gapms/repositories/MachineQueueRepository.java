package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.MachineType;
import fpt.g36.gapms.models.entities.MachineQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MachineQueueRepository extends JpaRepository<MachineQueue, Long> {
    @Query("SELECT mq FROM MachineQueue mq WHERE mq.machineId = :machineId " +
            "AND mq.deadline > :currentTime")
    Optional<MachineQueue> findActiveQueueByMachineId(@Param("machineId") Long machineId, @Param("currentTime") LocalDateTime currentTime);

    List<MachineQueue> findByMachineId(Long machineId);

    @Query("SELECT mq FROM MachineQueue mq WHERE mq.machineId = :machineId " +
            "AND ((mq.plannedStart <= :plannedEnd " +
            "AND mq.deadline >= :plannedStart) " +
            "OR (mq.plannedStart <= :deadline " +
            "AND mq.deadline >= :plannedStart))")
    List<MachineQueue> findOverlappingQueue(@Param("machineId") Long machineId,
                                            @Param("plannedStart") LocalDateTime plannedStart,
                                            @Param("deadline") LocalDateTime deadline,
                                            @Param("plannedEnd") LocalDateTime plannedEnd);

    Optional<MachineQueue> findByWorkOrderDetailId(Long workOrderDetailId);

    @Query("SELECT mq FROM MachineQueue mq " +
            "WHERE mq.machineId = :machineId " +
            "AND mq.machineType = :machineType")
    Optional<MachineQueue> findByMachineIdAndMachineType(@Param("machineId") Long machineId, @Param("machineType") MachineType machineType);

    @Query("SELECT mq FROM MachineQueue mq " +
            "WHERE mq.machineType = :machineType " +
            "AND mq.deadline > :currentTime " +
            "AND mq.deadline = " +
            "(SELECT MIN(mq2.deadline) " +
            "FROM MachineQueue mq2 WHERE mq2.machineType = :machineType " +
            "AND mq2.deadline > :currentTime)")
    List<MachineQueue> findQueuesWithEarliestDeadline(@Param("machineType") MachineType machineType,
                                                      @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT COUNT (DISTINCT mq.machineId) FROM MachineQueue mq " +
            "WHERE mq.machineType = :machineType " +
            "AND ((mq.plannedStart <= :deadline AND mq.deadline >= :plannedStart))")
    long countDistinctMachinesInQueueByType(@Param("machineType") MachineType machineType,
                                            @Param("plannedStart") LocalDateTime plannedStart,
                                            @Param("deadline") LocalDateTime deadline);
}
