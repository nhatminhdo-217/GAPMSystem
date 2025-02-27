package fpt.g36.gapms.controller;


import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.dto.RfqFormDTO;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.services.*;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/request-for-quotation")
public class RfqController {

    private final UserUtils userUtils;
    @Autowired
    private RfqService rfqService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RfqDetailService rfqDetailService;


    public RfqController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @GetMapping("/view-list")
    public String getViewList(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (optionalUser.isPresent()) {
                Optional<Company> company = companyService.findByUserId(optionalUser.get().getId());
                List<Rfq> rfqs = rfqService.getAllRfqsByUserId(optionalUser.get().getId());
                model.addAttribute("rfqs", rfqs);
                if (company.isPresent()) {
                    model.addAttribute("check-company", company.get());
                } else {
                    model.addAttribute("check-company", null);
                }
            }
        }

        userUtils.getOptionalUser(model);

        return "request-for-quotation/view-rfq";
    }

    @GetMapping("/rfq-form")
    public String getRfqForm(Model model) {
        userUtils.getOptionalUser(model);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (optionalUser.isPresent()) {
                Optional<Company> company = companyService.findByUserId(optionalUser.get().getId());
                System.err.println("Find company" + company);
                if (company.isPresent()) {
                    model.addAttribute("company", company.get());
                    System.err.println("Find company name" + company.get().getName());
                } else {
                    model.addAttribute("company", null);
                }
            }
        }
        RfqFormDTO rfqForm = new RfqFormDTO();
        rfqForm.setRfqDetails(new ArrayList<>());
        rfqForm.getRfqDetails().add(new RfqDetail()); // Thêm dòng đầu tiên mặc định
        model.addAttribute("rfqForm", rfqForm);
        model.addAttribute("products", productService.getAllProducts());
        /*model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("categories", categoryService.getAllCategories());*/
        return "request-for-quotation/add-new-rfq";
    }

    @PostMapping("/save")
    public String saveRfq(@ModelAttribute("rfqForm") RfqFormDTO rfqForm, RedirectAttributes redirectAttributes, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        Rfq rfq = new Rfq();
        rfq.setExpectDeliveryDate(rfqForm.getDesiredDeliveryDate());
        rfq.setCreateBy(optionalUser.get());
        rfq.setIsSent(BaseEnum.NOT_APPROVED);
        rfq.setIsApproved(SendEnum.NOT_SENT);
        Rfq savedRfq = rfqService.saveRfq(rfq);

        System.err.println(rfq);
        List<RfqDetail> rfqDetails = rfqForm.getRfqDetails();
        List<RfqDetail> validDetails = rfqDetails.stream()
                .filter(detail -> detail.getProduct() != null && detail.getBrand() != null &&
                        detail.getCate() != null && detail.getQuantity() != null)
                .peek(detail -> detail.setRfq(rfq))
                .collect(Collectors.toList());
        if (validDetails.isEmpty()) {
            model.addAttribute("company", new Company());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("errorMessage", "Không có chi tiết lô hàng hợp lệ để lưu.");
            return "request-for-quotation/add-new-rfq";
        }
        System.err.println(rfqDetails);
        rfqDetailService.saveRfqDetail(rfqDetails);

        redirectAttributes.addFlashAttribute("saveSuccessMessage", "Tạo thành công yêu cầu báo giá mới");
        return "redirect:/request-for-quotation/view-list";
    }


    @GetMapping("/brands")
    @ResponseBody
    public List<Brand> getBrandsByProductId(@RequestParam Long productId) {
        return brandService.getBrandsByProductId(productId);
    }

    @GetMapping("/categories")
    @ResponseBody
    public List<Category> getCategoriesByBrandId(@RequestParam Long brandId) {
        List<Category> categories = categoryService.getCategoriesByBrandId(brandId);
        // Đảm bảo chỉ trả về id và name, không kéo theo các quan hệ khác
        return categories.stream()
                .map(category -> {
                    Category simpleCategory = new Category();
                    simpleCategory.setId(category.getId());
                    simpleCategory.setName(category.getName());
                    return simpleCategory;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/get-product-row")
    public String getProductRow(@RequestParam("rowNumber") int rowNumber, Model model) {
        model.addAttribute("rowNumber", rowNumber);
        model.addAttribute("products", productService.getAllProducts()); // Đảm bảo truyền products
        return "request-for-quotation/product-row"; // Trả về fragment
    }


    @GetMapping("/rfq-detail/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteRfqDetail(@PathVariable("id") long rfqDetailId) {
        try {
            // Giả sử có service để xóa
            rfqDetailService.deleteRfqDetailById(rfqDetailId);
            return ResponseEntity.ok("Xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa thất bại");
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteRfq(@PathVariable("id") Long rfqId, Model model, RedirectAttributes redirectAttributes) {
        rfqService.deleteRfqById(rfqId);
        redirectAttributes.addFlashAttribute("deleteSuccessMessage", "Đã báo hủy lô hàng!");
        return "redirect:/request-for-quotation/view-list"; // Trả về fragment
    }


    @GetMapping("/edit")
    @ResponseBody
    public ResponseEntity<String> editRfq(@RequestParam("rfqId") Long rfqId,
                                          @RequestParam("desiredDeliveryDate") String newDate) {
        try {
            // viết đây
            return ResponseEntity.ok("Xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa thất bại");
        }
    }
}

/*@PostMapping("/updateDeliveryDate")
    @ResponseBody // Trả về JSON thay vì render view
    public String updateDeliveryDate(@RequestParam("rfqId") Long rfqId,
                                     @RequestParam("desiredDeliveryDate") String newDate) {
        try {
            // Tìm RFQ theo ID
            RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy RFQ với ID: " + rfqId));

            // Cập nhật ngày giao hàng
            rfq.setDesiredDeliveryDate(newDate);
            rfqRepository.save(rfq); // Lưu vào database

            return "success"; // Trả về chuỗi "success" để JavaScript xử lý
        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // Trả về "error" nếu có lỗi
        }
    }*/