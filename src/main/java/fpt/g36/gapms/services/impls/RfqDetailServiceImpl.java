package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.RfqDetail;
import fpt.g36.gapms.repositories.RfqDetailRepository;
import fpt.g36.gapms.services.RfqDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RfqDetailServiceImpl implements RfqDetailService {

    @Autowired
    private RfqDetailRepository  rfqDetailRepository;


    @Override
    public void saveRfqDetail(List<RfqDetail> rfqDetails) {
        rfqDetailRepository.saveAll(rfqDetails);
    }

    @Override
    public void deleteRfqDetailById(Long id) {
        rfqDetailRepository.deleteById(id);
    }

}
