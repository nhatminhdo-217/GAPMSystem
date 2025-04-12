package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.quotation.QuotationInfoDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInforCustomerDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationListDTO;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.*;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/quotation")
public class QuotationController {

    private final QuotationService quotationService;
    private final UserUtils userUtils;
    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final RfqService rfqService;

    public QuotationController(QuotationService quotationService, UserUtils userUtils, ProductService productService, BrandService brandService, CategoryService categoryService, RfqService rfqService) {
        this.quotationService = quotationService;
        this.userUtils = userUtils;
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.rfqService = rfqService;
    }

    @GetMapping("/list")
    public String getListQuotation(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String product,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        userUtils.getOptionalUser(model);

        Page<QuotationListDTO> quotations = quotationService.getAllQuotations(search, product, brand, category, page);

        model.addAttribute("quotations", quotations);
        model.addAttribute("search", search);
        model.addAttribute("selectedProduct", product);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedCategory", category);

        // Gửi danh sách filter options
        model.addAttribute("products", productService.getAllProductNames());
        model.addAttribute("brands", brandService.getAllBrandNames());
        model.addAttribute("categories", categoryService.getAllCategoryNames());


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
    public String postQuotationDetail(@PathVariable("id") Long id, Model model) {

        User currentUser = userUtils.getOptionalUserInfo();

        quotationService.updateQuotationStatus(id, currentUser);

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

        quotationService.approvedQuotation(rfqId);
        QuotationInforCustomerDTO quotationCustomer = quotationService.getQuotationCustomer(rfqId);

        userUtils.getOptionalUser(model);
        model.addAttribute("quotation_customer", quotationCustomer);
        redirectAttributes.addFlashAttribute("approved", "Bạn đã chấp nhận đơn báo giá");
        return "redirect:/purchase-order/customer/list";
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
