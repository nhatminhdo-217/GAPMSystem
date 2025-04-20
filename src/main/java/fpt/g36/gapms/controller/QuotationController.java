package fpt.g36.gapms.controller;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.quotation.*;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.services.*;
import fpt.g36.gapms.utils.NotificationUtils;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/quotation")
public class QuotationController {

    private final QuotationService quotationService;
    private final UserUtils userUtils;
    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final RfqService rfqService;
   private final MailService mailService;
   private final PurchaseOrderService purchaseOrderService;
    private final NotificationUtils notificationUtils;

    public QuotationController(QuotationService quotationService, UserUtils userUtils, ProductService productService, BrandService brandService, CategoryService categoryService, RfqService rfqService, MailService mailService, PurchaseOrderService purchaseOrderService, NotificationUtils notificationUtils) {
        this.quotationService = quotationService;
        this.userUtils = userUtils;
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.rfqService = rfqService;
        this.mailService = mailService;
        this.purchaseOrderService = purchaseOrderService;
        this.notificationUtils = notificationUtils;
    }

    @GetMapping("/list")
    public String getListQuotation(
            @RequestParam(defaultValue = "", required = false) String search,
            @RequestParam(required = false) String product,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BaseEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createAt", required = false) String sortField,
            @RequestParam(defaultValue = "desc", required = false) String sortDir,
            Model model) {

        userUtils.getOptionalUser(model);

        Page<QuotationDTO> quotations = quotationService.getAllQuotation(search, status, page, size, sortField, sortDir);

        model.addAttribute("quotations", quotations);
        model.addAttribute("search", search);
        model.addAttribute("selectedProduct", product);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", quotations.getTotalPages());
        model.addAttribute("totalItems", quotations.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        int totalPages = quotations.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = java.util.stream.IntStream.rangeClosed(0, totalPages - 1)
                    .boxed()
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Gửi danh sách filter options
        model.addAttribute("products", productService.getAllProductNames());
        model.addAttribute("brands", brandService.getAllBrandNames());
        model.addAttribute("categories", categoryService.getAllCategoryNames());
        model.addAttribute("statuses", quotationService.getAllQuotationStatuses());

        return "quotation/list_quotation";
    }

    @GetMapping("/detail/{id}")
    public String getQuotationDetail(@PathVariable("id") Long id, Model model) {

        QuotationInfoDTO quotation_detail = quotationService.getQuotationInfo(id);

        userUtils.getOptionalUser(model);
        User currentUser = userUtils.getOptionalUserInfo();

        model.addAttribute("quotation_detail", quotation_detail);
        model.addAttribute("currentUser", currentUser);

        return "quotation/quotation_detail";
    }

    @PostMapping("/detail/{id}")
    public String postQuotationDetail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

        User currentUser = userUtils.getOptionalUserInfo();
        Quotation quotation = quotationService.getQuotationById(id);
        quotationService.updateQuotationStatus(id, currentUser);
        mailService.sendQuotationEmail(quotation.getRfq().getCreateBy().getEmail(), quotation.getRfq().getCreateBy().getUsername(), quotation.getRfq().getId());
         if(quotation.getAccepted() == BaseEnum.WAIT_FOR_APPROVAL) {
             redirectAttributes.addFlashAttribute("quotation_submit", "Báo giá đã được xác nhận và gửi cho khách");
             notificationUtils.sentQuotationToSaleStaffToCustomer(quotation.getRfq().getId(), quotation.getRfq().getCreateBy().getId() );
         }
        return "redirect:/quotation/detail/" + id;
    }


    @GetMapping("/quotation-customer/{rfqId}")
    public String getQuotationCustomer(@PathVariable("rfqId") int rfqId,Model model) {

        QuotationInforCustomerDTO quotationCustomer = quotationService.getQuotationCustomer(rfqId);

        userUtils.getOptionalUser(model);

        model.addAttribute("quotation_customer", quotationCustomer);
        return "quotation/quotation-customer";
    }


    @GetMapping("/quotation-customer-approved/{rfq-id}")
    public String getQuotationCustomerApproved(@PathVariable("rfq-id") int rfqId,Model model, RedirectAttributes redirectAttributes) {

          Quotation quotation = quotationService.approvedQuotation(rfqId);
        QuotationInforCustomerDTO quotationCustomer = quotationService.getQuotationCustomer(rfqId);
         PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrderDetailByQuotationId(quotation.getId());
        userUtils.getOptionalUser(model);
        model.addAttribute("quotation_customer", quotationCustomer);
        redirectAttributes.addFlashAttribute("approved", "Bạn đã chấp nhận đơn báo giá");
        return "redirect:/purchase-order/customer/detail/" +purchaseOrder.getId();
    }
    @GetMapping("/quotation-customer-cancel/{rfq-id}")
    public String getQuotationCustomerCancel(@PathVariable("rfq-id") int rfqId, Model model, RedirectAttributes redirectAttributes) {

        quotationService.notApprovedQuotation(rfqId);
        QuotationInforCustomerDTO quotationCustomer = quotationService.getQuotationCustomer(rfqId);

        userUtils.getOptionalUser(model);
        model.addAttribute("quotation_customer", quotationCustomer);
        redirectAttributes.addFlashAttribute("cancel", "Bạn đã hủy đơn báo giá");
        return "redirect:/quotation/quotation-customer/"+rfqId;
    }

}
