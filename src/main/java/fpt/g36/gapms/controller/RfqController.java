package fpt.g36.gapms.controller;


import ch.qos.logback.classic.Logger;
import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.dto.RfqFormDTO;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.services.*;
import fpt.g36.gapms.services.BrandService;
import fpt.g36.gapms.utils.NotificationUtils;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private NotificationUtils notificationUtils;

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
    private Logger log;


    public RfqController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @GetMapping("/view-list")
    public String getViewList(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (optionalUser.isPresent()) {
                Company company = companyService.getCompanyByUserId(optionalUser.get().getId());
                Pageable pageable = PageRequest.of(page, size);
                Page<Rfq> rfqs = rfqService.getAllRfqsByUserId(optionalUser.get().getId(), pageable);
                rfqs.forEach(rfq -> {
                    System.err.println("User rfq" + rfq.getIsSent());
                });
                model.addAttribute("rfqs", rfqs.getContent());
                model.addAttribute("currentPage", rfqs.getNumber()); // Trang hiện tại
                model.addAttribute("totalPages", rfqs.getTotalPages());
                System.err.println("totalPages"+ rfqs.getTotalPages());// Tổng số trang
                model.addAttribute("totalItems", rfqs.getTotalElements()); // Tổng số bản ghi
                model.addAttribute("pageSize", size); // Số bản ghi mỗi trang
                model.addAttribute("check-company", company);
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
                Company company = companyService.getCompanyByUserId(optionalUser.get().getId());
                model.addAttribute("company", company);
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
        notificationUtils.addNewRfqToCustomerFromSale(savedRfq.getId());
        redirectAttributes.addFlashAttribute("saveSuccessMessage", "Tạo thành công yêu cầu báo giá mới");
        return "redirect:/request-for-quotation/view-list";
    }


    @GetMapping("/brands")
    @ResponseBody
    public ResponseEntity<List<Brand>> getBrandsByProductId(@RequestParam Long productId) {
        try {
            List<Brand> brands = brandService.getBrandsByProductId(productId);
            System.out.println("Processing getBrandsByProductId for productId: " + productId + ", Found " + (brands != null ? brands.size() : 0) + " brands");
            return ResponseEntity.ok(brands != null ? brands : new ArrayList<>()); // Trả về danh sách rỗng nếu null
        } catch (Exception e) {
            System.out.println("Error in getBrandsByProductId: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>()); // Trả về danh sách rỗng nếu lỗi
        }
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

    @GetMapping("/get-product-row-edit")
    public String getProductRowEdit(@RequestParam(required = false) String rowNumber, Model model) {
        try {
            int parsedRowNumber = rowNumber != null ? Integer.parseInt(rowNumber) : 1; // Mặc định là 1 nếu null
            model.addAttribute("rowNumber", parsedRowNumber);

            List<Product> products = productService.getAllProducts();
            List<Brand> brands = brandService.getAllBrands();
            List<Category> categories = categoryService.getAllCategories();

            System.out.println("Row number: " + parsedRowNumber);
            System.out.println("Products size: " + (products != null ? products.size() : "null"));
            System.out.println("Brands size: " + (brands != null ? brands.size() : "null"));
            System.out.println("Categories size: " + (categories != null ? categories.size() : "null"));

            if (products == null || products.isEmpty()) {
                throw new RuntimeException("No products found in database.");
            }
            if (brands == null || brands.isEmpty()) {
                throw new RuntimeException("No brands found in database.");
            }
            if (categories == null || categories.isEmpty()) {
                throw new RuntimeException("No categories found in database.");
            }

            model.addAttribute("products", products);
            model.addAttribute("brands", brands);
            model.addAttribute("categories", categories);
            return "request-for-quotation/product-row-edit";
        } catch (NumberFormatException e) {
            System.out.println(("Invalid rowNumber format: " + rowNumber));
            throw new RuntimeException("Invalid row number format: " + rowNumber, e);
        } catch (Exception e) {
            System.out.println("Error in getProductRowEdit: " + e.getMessage());
            throw new RuntimeException("Failed to load product row: " + e.getMessage(), e);
        }
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

    @GetMapping("/check-delete/{rfqId}/{detailId}")
    @ResponseBody
    public Map<String, Boolean> checkDeleteRfqDetail(@PathVariable Long rfqId, @PathVariable Long detailId) {
        Map<String, Boolean> response = new HashMap<>();
        try {
            Rfq rfq = rfqService.getRfqById(rfqId);
            if (rfq == null) {
                response.put("canDelete", false);
                return response;
            }

            // Kiểm tra số lượng RfqDetail trong Rfq (sử dụng stream để đếm các phần tử không null)
            long rfqDetailsCount = rfq.getRfqDetails().stream().filter(Objects::nonNull).count();
            response.put("canDelete", rfqDetailsCount > 1);
            return response;
        } catch (Exception e) {
            log.error("Error checking delete for RFQ detail: " + e.getMessage(), e);
            response.put("canDelete", false);
            return response;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteRfq(@PathVariable("id") Long rfqId, Model model, RedirectAttributes redirectAttributes) {
        rfqService.deleteRfqById(rfqId);
        redirectAttributes.addFlashAttribute("deleteSuccessMessage", "Đã báo hủy lô hàng!");
        return "redirect:/request-for-quotation/view-list"; // Trả về fragment
    }


    //update ngày mong muốn giao hàng thôi
    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<String> editRfq(@RequestParam("rfqId") Long rfqId,
                                          @RequestParam("desiredDeliveryDate") LocalDate newDate) {
        try {
            System.out.println(rfqId);
            System.out.println(newDate);
            Rfq rfq =  rfqService.editRfq(rfqId, newDate);
            System.out.println(rfq);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDate = newDate.format(formatter);
            return ResponseEntity.ok(String.valueOf(newDate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa thất bại");
        }
    }

    @GetMapping("/edit-rfq-form")
    public String showEditRfqForm(@RequestParam Long rfqId, Model model) {
        userUtils.getOptionalUser(model);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        try {
            Long userId = optionalUser.get().getId();
            Rfq rfq = rfqService.getRfqByIdAndUserId(rfqId, userId);
            model.addAttribute("rfq", rfq);

        } catch (RuntimeException e) {

            System.err.println("rfq null đấy: " + e.getMessage());
            throw new RuntimeException("Mã lô không hợp lệ: ", e); // Ném exception để xử lý ở @ControllerAdvice // Trả về form rỗng với lỗi
        }
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("categories", categoryService.getAllCategories());
        System.err.println("Products size: " + (productService.getAllProducts() != null ? productService.getAllProducts().size() : "null"));
        return "request-for-quotation/edit-rfq-form";
    }

    @PostMapping("/update-rfq")
    public String updateRfq(@ModelAttribute Rfq rfq, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        int index = 0;
        List<String> errorMessages = new ArrayList<>();
        for (RfqDetail detail : rfq.getRfqDetails()) {
            Product product = detail.getProduct();
            Brand brand = detail.getBrand();

            if (product == null || product.getId() == null) {
                bindingResult.rejectValue("rfqDetails[" + index + "].product", "error.rfqDetails", "Sản phẩm không được để trống");
                System.err.println("Sản phẩm null tại index " + index);
            } else {
                Product fullProduct = productService.getProductById(product.getId());
                if (fullProduct == null) {
                    bindingResult.rejectValue("rfqDetails[" + index + "].product", "error.rfqDetails", "Sản phẩm không tồn tại");
                } else {
                    detail.setProduct(fullProduct);
                }
            }

            if (brand == null || brand.getId() == null) {
                bindingResult.rejectValue("rfqDetails[" + index + "].brand", "error.rfqDetails", "Thương hiệu không được để trống");
                System.err.println("Thương hiệu null tại index " + index);
            } else {
                Brand fullBrand = brandService.getBrandById(brand.getId());
                if (fullBrand == null) {
                    bindingResult.rejectValue("rfqDetails[" + index + "].brand", "error.rfqDetails", "Thương hiệu không tồn tại");
                } else {
                    detail.setBrand(fullBrand);
                }
            }

            if (detail.getProduct() != null && detail.getBrand() != null) {
                if (!detail.getProduct().getBrands().contains(detail.getBrand())) {
                    String errorMessage = "Thương hiệu " + detail.getBrand().getName() + " không thuộc sản phẩm " + detail.getProduct().getName();
                    bindingResult.rejectValue("rfqDetails[" + index + "].brand", "error.rfqDetails", errorMessage);
                    errorMessages.add(errorMessage);

                    System.err.println("Đã thêm specificErrorMessage: " + errorMessage); // Log để kiểm tra
                }
            }
            index++;
            model.addAttribute("specificErrorMessage", errorMessages);
        }

        if (bindingResult.hasErrors()) {
            userUtils.getOptionalUser(model);
            System.err.println("Có lỗi ở RFQ ID: " + rfq.getId());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "request-for-quotation/edit-rfq-form"; // Trả lại form
        }

        rfqDetailService.editRfqDetail(rfq);
        redirectAttributes.addFlashAttribute("editRfqDetailSuccessMessage", "Cập nhật thành công yêu cầu báo giá");
        return "redirect:/request-for-quotation/view-list";
    }


    /*@GetMapping("/edit/brands")
    public ResponseEntity<List<Brand>> getBrandsByProductIdEdit(@RequestParam("productId") Long productId) {
        Product product = productService.getProductById(productId);
        List<Brand> brands = new ArrayList<>(product.getBrands()); // Lấy danh sách Brand từ Product
        return ResponseEntity.ok(brands);
    }*/
}

