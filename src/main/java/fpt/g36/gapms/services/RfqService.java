package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.Rfq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RfqService {

    Page<Rfq> getAllRfqsByUserId(Long userId, Pageable pageable);
    List<Rfq> getAllRfqsByUserId(Long userId);

    Rfq saveRfq(Rfq rfq);

    void deleteRfqById(Long rfqId);

    Rfq  getRfqById(Long rfqId);

    Rfq editRfq(Long rfqId, LocalDate newDate);

    List<Rfq> getAllApprovedRfqs();

    Rfq getRfqBySolutionId(Long solutionId);

    List<Rfq> getAllRfq();

    Rfq submitRfq(Long rfqId, Long userId);
}
