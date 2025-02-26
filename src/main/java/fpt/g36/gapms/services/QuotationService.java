package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.quotation.QuotationInfoDTO;

public interface QuotationService {

    QuotationInfoDTO getQuotationInfo(long id);
}
