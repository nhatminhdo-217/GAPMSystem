package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.Rfq;

import java.util.List;

public interface RfqService {

    List<Rfq> getAllRfqsByUserId(Long userId);

    Rfq saveRfq(Rfq rfq);

    void deleteRfqById(Long rfqId);

    Rfq  getRfqById(Long rfqId);;

    List<Rfq> getAllApprovedRfqs();
}
