package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.RfqDetail;

import java.util.List;

public interface RfqDetailService {

    /*List<RfqDetailService> getAllRfqDetail();*/

    void saveRfqDetail(List<RfqDetail > rfqDetails);

    void deleteRfqDetailById(Long id);

}
