package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.RfqDetail;

import java.util.List;

public interface RfqDetailService {

    /*List<RfqDetailService> getAllRfqDetail();*/

    void saveRfqDetail(List<RfqDetail > rfqDetails);

    void deleteRfqDetailById(Long id);

    RfqDetail getRfqDetailById(Long rfqDetailId);

    void editRfqDetail(Rfq rfq);

    List<RfqDetail> getAllRfqDetailByRfqId(Long rfqId);

}
