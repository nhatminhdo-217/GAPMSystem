package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.quotation.QuotationDetailDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoProjection;
import fpt.g36.gapms.repositories.QuotationRepository;
import fpt.g36.gapms.services.QuotationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository quotationRepository;

    public QuotationServiceImpl(QuotationRepository quotationRepository) {
        this.quotationRepository = quotationRepository;
    }

    @Override
    public QuotationInfoDTO getQuotationInfo(long id) {
        List<QuotationInfoProjection> quotationDetail = quotationRepository.findQuotationDetail(id);

        if (quotationDetail.isEmpty()) {
            throw new RuntimeException("Quotation not found");
        }

        QuotationInfoDTO quotationInfoDTO = new QuotationInfoDTO();
        quotationInfoDTO.setQuotationId(quotationDetail.get(0).getQuotationId());
        quotationInfoDTO.setUserName(quotationDetail.get(0).getUserName());
        quotationInfoDTO.setCompanyName(quotationDetail.get(0).getCompanyName());
        quotationInfoDTO.setTaxNumber(quotationDetail.get(0).getTaxNumber());
        quotationInfoDTO.setProductName(quotationDetail.get(0).getProductName());
        quotationInfoDTO.setExpectedDate(quotationDetail.get(0).getExpectedDate());
        quotationInfoDTO.setActualDate(quotationDetail.get(0).getActualDate());
        quotationInfoDTO.setReason(quotationDetail.get(0).getReason());

        List<QuotationDetailDTO> products = quotationDetail.stream()
                .map(p -> {
                    QuotationDetailDTO product = new QuotationDetailDTO();
                    product.setBrandName(p.getBrandName());
                    product.setCategoryName(p.getCategoryName());
                    product.setHasColor(p.isHasColor());
                    product.setPrice(p.getPrice());
                    product.setNoteColor(p.getNoteColor());
                    return product;
                })
                .toList();

        quotationInfoDTO.setProducts(products);
        return quotationInfoDTO;
    }
}
