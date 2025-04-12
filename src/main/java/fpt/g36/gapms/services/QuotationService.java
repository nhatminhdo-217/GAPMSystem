package fpt.g36.gapms.services;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.quotation.QuotationDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInforCustomerDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationListDTO;
import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QuotationService {

    QuotationInfoDTO getQuotationInfo(long id);

    Page<QuotationListDTO> getAllQuotations(String search, String product, String brand, String category, String status, int page, int size, String sortField, String sortDir);

    QuotationInforCustomerDTO getQuotationCustomer(long rfqId);

    void approvedQuotation(long quotationId);

    void notApprovedQuotation(long quotationId);

    void createQuotationByRfqId(long rfqId);

    Long getQuotationIdByRfqId(long rfqId);

    void updateQuotationStatus(Long id, User currentUser);

    List<BaseEnum> getAllQuotationStatuses();

    Page<QuotationDTO> getAllQuotation(String search, BaseEnum status, int page, int size, String sortField, String sortDir);
}
