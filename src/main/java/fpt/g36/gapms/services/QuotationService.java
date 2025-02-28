package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.quotation.QuotationInfoDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationListDTO;
import fpt.g36.gapms.models.entities.Quotation;
import org.springframework.data.domain.Page;

public interface QuotationService {

    QuotationInfoDTO getQuotationInfo(long id);

    Page<QuotationListDTO> getAllQuotations(String search, String product, String brand, String category, int page);
}
