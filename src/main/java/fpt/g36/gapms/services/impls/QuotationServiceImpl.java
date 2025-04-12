package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.dto.quotation.*;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.repositories.*;
import fpt.g36.gapms.services.CateBrandPriceService;
import fpt.g36.gapms.services.QuotationService;
import fpt.g36.gapms.services.RfqDetailService;
import fpt.g36.gapms.services.RfqService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository quotationRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CateBrandPriceService cateBrandPriceService;
    private final RfqService rfqService;
    private final RfqDetailService rfqDetailService;
    private PurchaseOrderRepository purchaseOrderRepository;
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    public QuotationServiceImpl(QuotationRepository quotationRepository, BrandRepository brandRepository, CategoryRepository categoryRepository, ProductRepository productRepository, CateBrandPriceService cateBrandPriceService, PurchaseOrderRepository purchaseOrderRepository, RfqService rfqService, RfqDetailService rfqDetailService,PurchaseOrderDetailRepository purchaseOrderDetailRepository) {

        this.quotationRepository = quotationRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.cateBrandPriceService = cateBrandPriceService;
        this.rfqService = rfqService;
        this.purchaseOrderRepository  = purchaseOrderRepository;
        this.rfqDetailService = rfqDetailService;
        this.purchaseOrderDetailRepository  = purchaseOrderDetailRepository;
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
        quotationInfoDTO.setCompanyAddress(quotationDetail.get(0).getCompanyAddress());
        quotationInfoDTO.setIsAccepted(quotationDetail.get(0).getIsAccepted().name());
        quotationInfoDTO.setSolutionId(quotationDetail.get(0).getSolutionId());
        quotationInfoDTO.setExpectedDate(quotationDetail.get(0).getExpectedDate());
        quotationInfoDTO.setActualDate(quotationDetail.get(0).getActualDate());

        List<QuotationDetailDTO> products = quotationDetail.stream()
                .map(p -> {
                    QuotationDetailDTO product = new QuotationDetailDTO();
                    product.setProductName(p.getProductName());
                    product.setBrandName(p.getBrandName());
                    product.setCategoryName(p.getCategoryName());
                    product.setPrice(p.getPrice());
                    product.setNoteColor(p.getNoteColor());
                    product.setQuantity(p.getQuantity());
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
            BaseEnum isAccepted = BaseEnum.valueOf((String) row[2]);
            String productName = (String) row[3];
            String brandName = (String) row[4];
            String categoryName = (String) row[5];
            BigDecimal price = (BigDecimal) row[6];
            String noteColor = (String) row[7];
            Timestamp timestamp = (Timestamp) row[8];
            LocalDate createAt = timestamp.toLocalDateTime().toLocalDate();
            Integer quantity = (Integer) row[9];

            quotationMap.computeIfAbsent(quotationId, id -> {
                QuotationListDTO dto = new QuotationListDTO();
                dto.setQuotationId(quotationId);
                dto.setUserName(userName);
                dto.setIsAccepted(isAccepted);
                dto.setCreateAt(createAt);
                dto.setProducts(new ArrayList<>());
                return dto;
            });

            QuotationDetailDTO productDetail = new QuotationDetailDTO();
            productDetail.setProductName(productName);
            productDetail.setBrandName(brandName);
            productDetail.setCategoryName(categoryName);
            productDetail.setPrice(price);
            productDetail.setNoteColor(noteColor);
            productDetail.setQuantity(quantity);
            quotationMap.get(quotationId).getProducts().add(productDetail);
        }

        List<QuotationListDTO> content = new ArrayList<>(quotationMap.values());
        return new PageImpl<>(content, pageable, rawResults.getTotalElements());
    }

    @Override
    public QuotationInforCustomerDTO getQuotationCustomer(long rfqId) {
        List<QuotationInforCustomerProjection> quotationDetail = new ArrayList<>();

        /*List<QuotationInforCustomerProjection> quotationDetail = quotationRepository.findQuotationCustomer(rfqId);*/
         Rfq rfq = rfqService.getRfqById(rfqId);
        for (RfqDetail rfqDetail: rfq.getRfqDetails()) {
            Boolean color;
            Boolean checkColor = rfqDetail.getNoteColor().equalsIgnoreCase("#FFFFFF");
            if(checkColor) {
                color = false;
                QuotationInforCustomerProjection quotationDetail_set = quotationRepository.findQuotationCustomers(rfqDetail.getId(), rfqDetail.getBrand().getId(),rfqDetail.getCate().getId(), color);
                quotationDetail.add(quotationDetail_set);
            }else {
                color = true;
                QuotationInforCustomerProjection quotationDetail_set = quotationRepository.findQuotationCustomers(rfqDetail.getId(),rfqDetail.getBrand().getId(),rfqDetail.getCate().getId(), color);
                quotationDetail.add(quotationDetail_set);
            }

        }

        QuotationInforCustomerDTO quotationInforCustomerDTO = new QuotationInforCustomerDTO();

        quotationInforCustomerDTO.setQuotationId(quotationDetail.get(0).getQuotationId());
        quotationInforCustomerDTO.setRfqId(quotationDetail.get(0).getRfqId());
        quotationInforCustomerDTO.setCancel(quotationDetail.get(0).getIsCanceled());
        quotationInforCustomerDTO.setAccepted(quotationDetail.get(0).getIsAccepted());
        quotationInforCustomerDTO.setUserName(quotationDetail.get(0).getUserName());
        quotationInforCustomerDTO.setCompanyName(quotationDetail.get(0).getCompanyName());
        quotationInforCustomerDTO.setTaxNumber(quotationDetail.get(0).getTaxNumber());
        quotationInforCustomerDTO.setProductName(quotationDetail.get(0).getProductName());
        quotationInforCustomerDTO.setExpectedDate(quotationDetail.get(0).getExpectedDate());
        quotationInforCustomerDTO.setActualDate(quotationDetail.get(0).getActualDate());
        quotationInforCustomerDTO.setReason(quotationDetail.get(0).getReason());

        List<QuotationCustomerDTO> products = quotationDetail.stream()
                .map(p -> {
                    QuotationCustomerDTO product = new QuotationCustomerDTO();
                    product.setProductName(p.getProductName());
                    product.setBrandName(p.getBrandName());
                    product.setCategoryName(p.getCategoryName());
                    product.setColor(p.getIsColor());
                    /*product.setColor(p.getIsColor());*/
                    product.setPrice(p.getPrice());
                    product.setQuantity(p.getQuantity());
                    product.setNoteColor(p.getNoteColor());
                    return product;
                })
                .toList();

        quotationInforCustomerDTO.setProducts(products);
        return quotationInforCustomerDTO;
    }

    @Override
    public void approvedQuotation(long rfqId) {
        Quotation quotation = quotationRepository.findByRfqId(rfqId);
        if (quotation == null) {
            throw new RuntimeException("Quotation not found");
        }
        quotation.setIsAccepted(BaseEnum.APPROVED);
        quotationRepository.save(quotation);

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setQuotation(quotation);
        purchaseOrder.setStatus(BaseEnum.DRAFT);
        purchaseOrder.setCustomer(quotation.getRfq().getCreateBy());
       PurchaseOrder purchaseOrderSaved = purchaseOrderRepository.save(purchaseOrder);

        List<RfqDetail> rfqDetails = rfqDetailService.getAllRfqDetailByRfqId(rfqId);

        for(RfqDetail rfqDetail : rfqDetails){
            PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
            purchaseOrderDetail.setQuantity(rfqDetail.getQuantity());
            purchaseOrderDetail.setBrand(rfqDetail.getBrand());
            purchaseOrderDetail.setCategory(rfqDetail.getCate());
            purchaseOrderDetail.setProduct(rfqDetail.getProduct());
            purchaseOrderDetail.setPurchaseOrder(purchaseOrderSaved);
            purchaseOrderDetail.setNote_color(rfqDetail.getNoteColor());
            Boolean checkColor = rfqDetail.getNoteColor().equalsIgnoreCase("#FFFFFF");
            BigDecimal unitPrice;
            if(checkColor) {

                 unitPrice = cateBrandPriceService.getPriceByBrandIdAndCateIdAndIsColor(rfqDetail.getBrand().getId(),rfqDetail.getCate().getId(),false);
            }else {
                 unitPrice = cateBrandPriceService.getPriceByBrandIdAndCateIdAndIsColor(rfqDetail.getBrand().getId(),rfqDetail.getCate().getId(),true);
            }

            /*BigDecimal unitPrice = cateBrandPriceService.getPriceByBrandIdAndCateIdAndIsColor(rfqDetail.getBrand().getId(),rfqDetail.getCate().getId(),checkColor);*/
            purchaseOrderDetail.setUnitPrice(unitPrice);
            purchaseOrderDetail.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(rfqDetail.getQuantity())));
            purchaseOrderDetailRepository.save(purchaseOrderDetail);

        }


    }

    @Override
    public void notApprovedQuotation(long rfqId) {
        Quotation quotation = quotationRepository.findByRfqId(rfqId);
        if (quotation == null) {
            throw new RuntimeException("Quotation not found");
        }
        quotation.setIsAccepted(BaseEnum.CANCELED);
        quotationRepository.save(quotation);
    }

    @Override
    @Transactional
    public void createQuotationByRfqId(long rfqId) {
        Rfq rfq = rfqService.getRfqById(rfqId);

        if (rfq == null) {
            throw new IllegalArgumentException("createQuotationByRfqId: " + "RFQ không tồn tại");
        }

        Solution solution = rfq.getSolution();
        if (solution == null || !solution.getIsSent().equals(SendEnum.SENT)) {
            throw new IllegalArgumentException("createQuotationByRfqId: " + "Solution không tồn tại hoặc chưa được gửi");
        }

        Quotation quotation = new Quotation();
        quotation.setIsCanceled(false);
        quotation.setIsAccepted(BaseEnum.DRAFT);
        quotation.setRfq(rfq);

        quotationRepository.save(quotation);
    }

    @Override
    public Long getQuotationIdByRfqId(long rfqId) {
        return quotationRepository.findQuotationIdByRfqId(rfqId);
    }

    @Override
    public void updateQuotationStatus(Long id, User currentUser) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        if (getStatusByQuotationId(id) == BaseEnum.DRAFT) {
            quotation.setIsAccepted(BaseEnum.NOT_APPROVED);
            quotation.setUpdateAt(LocalDateTime.now());
            quotation.setCreatedBy(currentUser);
        }  else if (getStatusByQuotationId(id) == BaseEnum.NOT_APPROVED){
            quotation.setIsAccepted(BaseEnum.WAIT_FOR_APPROVAL);
            quotation.setUpdateAt(LocalDateTime.now());
        }else {
            throw new RuntimeException("Quotation status cannot valid");
        }
        quotationRepository.save(quotation);
    }

    private BaseEnum getStatusByQuotationId(Long id) {
        Optional<Quotation> quotation = quotationRepository.findById(id);
        return quotation.map(Quotation::getIsAccepted).orElse(null);
    }
}
