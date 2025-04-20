package fpt.g36.gapms.models.mapper;

import fpt.g36.gapms.models.dto.quotation.QuotationDTO;
import fpt.g36.gapms.models.entities.Quotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuotationMapper {

    public List<QuotationDTO> toListDTO(Page<Quotation> quotations) {

        List<Quotation> listQuotation = quotations.getContent();

        return listQuotation.stream()
                .map(this::toDTO)
                .toList();
    }

    public QuotationDTO toDTO(Quotation quotation) {
        if (quotation == null) {
            return null;
        }

        QuotationDTO dto = new QuotationDTO();
        dto.setId(quotation.getId());
        dto.setRfqId(quotation.getRfq().getId());
        dto.setIsAccepted(quotation.getIsAccepted());
        dto.setCreateBy(quotation.getCreatedBy() != null ? quotation.getCreatedBy().getUsername() : null);
        dto.setCustomerName(quotation.getRfq().getCreateBy().getUsername());
        dto.setCreateAt(quotation.getCreateAt());

        return dto;
    }
}
