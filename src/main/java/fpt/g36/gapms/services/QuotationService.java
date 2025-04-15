package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.quotation.QuotationInfoDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInforCustomerDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationListDTO;
import fpt.g36.gapms.models.entities.Quotation;
import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;

public interface QuotationService {

    QuotationInfoDTO getQuotationInfo(long id);

    Page<QuotationListDTO> getAllQuotations(String search, String product, String brand, String category, int page);

    QuotationInforCustomerDTO getQuotationCustomer(long rfqId);

    Quotation approvedQuotation(long quotationId);

    void notApprovedQuotation(long quotationId);

    void createQuotationByRfqId(long rfqId);

    Long getQuotationIdByRfqId(long rfqId);

    void updateQuotationStatus(Long id, User currentUser);


    Quotation getQuotationById(Long quotationId);
}
