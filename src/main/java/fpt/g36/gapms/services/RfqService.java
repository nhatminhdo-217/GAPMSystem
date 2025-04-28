package fpt.g36.gapms.services;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.Rfq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RfqService {

    Page<Rfq> getAllRfqsByUserId(Long userId, Pageable pageable);

    Rfq saveRfq(Rfq rfq);

    void deleteRfqById(Long rfqId);

    Rfq getRfqById(Long rfqId);

    Rfq getRfqByIdAndUserId(Long rfqId, Long UserId);

    Rfq editRfq(Long rfqId, LocalDate newDate);

    Rfq submitRfq(Long rfqId, Long userId, LocalDate deadlineSolution);

    Page<Rfq> getRfqsByStatus(BaseEnum status, Pageable pageable);

    //
    Page<Rfq> getApprovedRfqsWithoutSolution(Pageable pageable);

    Page<Rfq> getApprovedRfqsWithSolution(Pageable pageable);
    //

    Rfq getRfqByIdAndStatus(Long rfqId, BaseEnum status);

    Rfq getApprovedRfqWithoutSolutionById(Long rfqId);

    Rfq getApprovedRfqWithSolutionById(Long rfqId);

}
