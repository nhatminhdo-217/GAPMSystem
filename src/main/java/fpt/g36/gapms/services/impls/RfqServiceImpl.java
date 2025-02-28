package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.repositories.RfqRepository;
import fpt.g36.gapms.services.RfqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RfqServiceImpl implements RfqService {
    @Autowired
    private RfqRepository rfqRepository;


    @Override
    public Page<Rfq> getAllRfqsByUserId(Long userId, Pageable pageable) {
        return rfqRepository.getRfqByUserId(userId, pageable);
    }
        @Override
    public List<Rfq> getAllRfqsByUserId(Long userId) {
        return rfqRepository.getRfqByUserId(userId);
    }

    @Override
    public Rfq saveRfq(Rfq rfq) {
        return rfqRepository.save(rfq);
    }

    @Override
    public void deleteRfqById(Long rfqId) {
        rfqRepository.deleteById(rfqId);
    }

    @Transactional
    @Override
    public Rfq getRfqById(Long rfqId) {
        return rfqRepository.findById(rfqId).orElseThrow(() -> new RuntimeException("Rfq not found"));
    }

    @Override
    public Rfq editRfq(Long rfqId, LocalDate newDate) {
        Rfq rfq = rfqRepository.findById(rfqId).orElse(null);
        if (rfq != null) {
            rfq.setExpectDeliveryDate(newDate);

            rfq.setUpdateAt(LocalDateTime.now());
            return rfqRepository.save(rfq);
        }
        return null;
    }
    @Override
    public List<Rfq> getAllApprovedRfqs() {
        return rfqRepository.getAllApprovedRfqs(BaseEnum.APPROVED);
    }
}
