package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.quotation.QuotationDetailDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoProjection;
import fpt.g36.gapms.models.dto.quotation.QuotationListDTO;
import fpt.g36.gapms.models.entities.Quotation;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.RfqDetail;
import fpt.g36.gapms.repositories.*;
import fpt.g36.gapms.services.CateBrandPriceService;
import fpt.g36.gapms.services.QuotationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository quotationRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CateBrandPriceService cateBrandPriceService;

    public QuotationServiceImpl(QuotationRepository quotationRepository, BrandRepository brandRepository, CategoryRepository categoryRepository, ProductRepository productRepository, CateBrandPriceService cateBrandPriceService) {
        this.quotationRepository = quotationRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.cateBrandPriceService = cateBrandPriceService;
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
                    product.setColor(p.getIsColor());
                    product.setPrice(p.getPrice());
                    product.setNoteColor(p.getNoteColor());
                    return product;
                })
                .toList();

        quotationInfoDTO.setProducts(products);
        return quotationInfoDTO;
    }

    @Override
    public Page<QuotationListDTO> getAllQuotations(String search, String product, String brand, String category, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Object[]> rawResults = quotationRepository.findAllWithFilters(search, product, brand, category, pageable);


        Map<Long, QuotationListDTO> quotationMap = new HashMap<>();
        for (Object[] row : rawResults.getContent()) {
            Long quotationId = (Long) row[0];
            String userName = (String) row[1];
            String productName = (String) row[2];
            String brandName = (String) row[3];
            String categoryName = (String) row[4];
            Boolean isColor = (Boolean) row[5];
            BigDecimal price = (BigDecimal) row[6];
            String noteColor = (String) row[7];

            quotationMap.computeIfAbsent(quotationId, id -> {
                QuotationListDTO dto = new QuotationListDTO();
                dto.setQuotationId(quotationId);
                dto.setUserName(userName);
                dto.setProducts(new ArrayList<>());
                return dto;
            });

            QuotationDetailDTO productDetail = new QuotationDetailDTO();
            productDetail.setProductName(productName);
            productDetail.setBrandName(brandName);
            productDetail.setCategoryName(categoryName);
            productDetail.setColor(isColor);
            productDetail.setPrice(price);
            productDetail.setNoteColor(noteColor);
            quotationMap.get(quotationId).getProducts().add(productDetail);
        }

        List<QuotationListDTO> content = new ArrayList<>(quotationMap.values());
        return new PageImpl<>(content, pageable, rawResults.getTotalElements());
    }
}
