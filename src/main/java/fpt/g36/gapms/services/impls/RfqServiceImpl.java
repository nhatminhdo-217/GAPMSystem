package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.repositories.RfqRepository;
import fpt.g36.gapms.services.RfqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RfqServiceImpl implements RfqService {
    @Autowired
    private RfqRepository rfqRepository;


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
        Optional<Rfq> rfqOptional = rfqRepository.findById(rfqId);
        return rfqOptional.orElseThrow(() -> new RuntimeException("Rfq not found"));
    }

    @Override
    public List<Rfq> getAllApprovedRfqs() {
        return rfqRepository.getAllApprovedRfqs(BaseEnum.APPROVED);
    }
}
