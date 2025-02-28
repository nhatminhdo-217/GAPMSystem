package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.quotation.QuotationDetailDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoProjection;
import fpt.g36.gapms.models.dto.quotation.QuotationListDTO;
import fpt.g36.gapms.models.entities.Quotation;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.RfqDetail;
import fpt.g36.gapms.repositories.QuotationRepository;
import fpt.g36.gapms.services.QuotationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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

    @Override
    public Page<QuotationListDTO> getQuotationSortedByCreateAt(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Quotation> quotationPage = quotationRepository.findQuotationsByCreateAt(pageable);

    return quotationPage.map(this::mapToQuotationListDTO);
    }

    private QuotationListDTO mapToQuotationListDTO(Quotation quotation) {

        QuotationListDTO listQuotation = new QuotationListDTO();

        listQuotation.setQuotationId(quotation.getId());

        Rfq rfq = quotation.getRfq();
        Set<RfqDetail> rfqDetail = rfq.getRfqDetails();
        if (rfq != null) {
            listQuotation.setUserName(rfq.getCreateBy().getUsername());
        }

        List<QuotationDetailDTO> detailDTO = rfqDetail.stream()
                .map(rfqDetail1 -> {
                    QuotationDetailDTO detail = new QuotationDetailDTO();
                    detail.setProductName(rfqDetail1.getProduct().getName());
                    detail.setBrandName(rfqDetail1.getProduct().);
                    detail.setCategoryName(rfqDetail1.getProduct().getCategory().getName());
                    detail.setHasColor(rfqDetail1.getProduct().isHasColor());
                    detail.setPrice(quotation.getPrice());
                    detail.setNoteColor(quotation.getNoteColor());
                    return detail;
                })
                .collect(Collectors.toList());

        return null;
    }
}
