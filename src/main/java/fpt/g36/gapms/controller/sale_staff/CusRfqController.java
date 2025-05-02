package fpt.g36.gapms.controller.sale_staff;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.CompanyService;
import fpt.g36.gapms.services.RfqService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sale-staff")
public class CusRfqController {
    @Autowired
    private final RfqService rfqService;
    @Autowired
    private final UserUtils userUtils;
    @Autowired
    private final UserService userService;
    @Autowired
    private final CompanyService companyService;

    public CusRfqController(RfqService rfqService, UserUtils userUtils, UserService userService, CompanyService companyService) {
        this.rfqService = rfqService;
        this.userUtils = userUtils;
        this.userService = userService;
        this.companyService = companyService;
    }

    @GetMapping("/view-all-cus-rfq")
    public String getRfqViewList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "not-approved-content") String activeTab,
            @RequestParam(required = false) String previousTab,
            Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = principal.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (!optionalUser.isPresent()) {
                System.err.println("Không tìm thấy User với email/phone: " + emailOrPhone + ", chuyển hướng đến trang login.");
                return "redirect:/login";
            }
            Pageable pageable = PageRequest.of(page, size);

            Page<Rfq> rfqsNotApproved;
            Page<Rfq> rfqsApprovedNoSolution;
            Page<Rfq> rfqsApprovedWithSolution;

            // Xử lý tìm kiếm theo ID
            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    try {
                        // Tìm trong tab 1: Chưa phê duyệt
                        Rfq rfq = rfqService.getRfqByIdAndStatus(searchId, BaseEnum.NOT_APPROVED);
                        rfqsNotApproved = new PageImplWrapper<>(Collections.singletonList(rfq), pageable, 1);
                        rfqsApprovedNoSolution = rfqService.getApprovedRfqsWithoutSolution(pageable); // Giữ nguyên dữ liệu tab 2
                        rfqsApprovedWithSolution = rfqService.getApprovedRfqsWithSolution(pageable); // Giữ nguyên dữ liệu tab 3
                        activeTab = "not-approved-content";
                        model.addAttribute("previousTab", activeTab);
                    } catch (RuntimeException e1) {
                        try {
                            // Tìm trong tab 2: Đã phê duyệt (Chưa có giải pháp)
                            Rfq rfq = rfqService.getApprovedRfqWithoutSolutionById(searchId);
                            rfqsNotApproved = rfqService.getRfqsByStatus(BaseEnum.NOT_APPROVED, pageable); // Giữ nguyên dữ liệu tab 1
                            rfqsApprovedNoSolution = new PageImplWrapper<>(Collections.singletonList(rfq), pageable, 1);
                            rfqsApprovedWithSolution = rfqService.getApprovedRfqsWithSolution(pageable); // Giữ nguyên dữ liệu tab 3
                            activeTab = "approved-no-solution-content";
                            model.addAttribute("previousTab", activeTab);
                        } catch (RuntimeException e2) {
                            try {
                                // Tìm trong tab 3: Đã phê duyệt (Có giải pháp)
                                Rfq rfq = rfqService.getApprovedRfqWithSolutionById(searchId);
                                rfqsNotApproved = rfqService.getRfqsByStatus(BaseEnum.NOT_APPROVED, pageable); // Giữ nguyên dữ liệu tab 1
                                rfqsApprovedNoSolution = rfqService.getApprovedRfqsWithoutSolution(pageable); // Giữ nguyên dữ liệu tab 2
                                rfqsApprovedWithSolution = new PageImplWrapper<>(Collections.singletonList(rfq), pageable, 1);
                                activeTab = "approved-with-solution-content";
                                model.addAttribute("previousTab", activeTab);
                            } catch (RuntimeException e3) {
                                // Không tìm thấy RFQ
                                rfqsNotApproved = rfqService.getRfqsByStatus(BaseEnum.NOT_APPROVED, pageable);
                                rfqsApprovedNoSolution = rfqService.getApprovedRfqsWithoutSolution(pageable);
                                rfqsApprovedWithSolution = rfqService.getApprovedRfqsWithSolution(pageable);
                                model.addAttribute("error", "Không tìm thấy RFQ với ID: " + searchId);
                                activeTab = (previousTab != null && !previousTab.isEmpty()) ? previousTab : "not-approved-content";
                                model.addAttribute("previousTab", activeTab);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Xử lý trường hợp ID không hợp lệ
                    model.addAttribute("error", "Mã RFQ phải là số.");
                    rfqsNotApproved = rfqService.getRfqsByStatus(BaseEnum.NOT_APPROVED, pageable);
                    rfqsApprovedNoSolution = rfqService.getApprovedRfqsWithoutSolution(pageable);
                    rfqsApprovedWithSolution = rfqService.getApprovedRfqsWithSolution(pageable);
                    activeTab = (previousTab != null && !previousTab.isEmpty()) ? previousTab : "not-approved-content";
                    model.addAttribute("previousTab", activeTab);
                }
            } else {
                // Nếu không có tìm kiếm, lấy dữ liệu đầy đủ cho tất cả các tab
                rfqsNotApproved = rfqService.getRfqsByStatus(BaseEnum.NOT_APPROVED, pageable);
                rfqsApprovedNoSolution = rfqService.getApprovedRfqsWithoutSolution(pageable);
                rfqsApprovedWithSolution = rfqService.getApprovedRfqsWithSolution(pageable);
                model.addAttribute("previousTab", activeTab);
            }

            model.addAttribute("rfqsNotApproved", rfqsNotApproved.getContent());
            model.addAttribute("rfqsNotApprovedPage", rfqsNotApproved);
            model.addAttribute("rfqsApprovedNoSolution", rfqsApprovedNoSolution.getContent());
            model.addAttribute("rfqsApprovedNoSolutionPage", rfqsApprovedNoSolution);
            model.addAttribute("rfqsApprovedWithSolution", rfqsApprovedWithSolution.getContent());
            model.addAttribute("rfqsApprovedWithSolutionPage", rfqsApprovedWithSolution);
            model.addAttribute("search", search);
            model.addAttribute("activeTab", activeTab);

            return "sale-staff/view-all-cus-rfq";
        }
        return "redirect:/login";
    }

    @GetMapping("/cus-rfq-details/{id}")
    public String getRfqDetailsView(@PathVariable Long id, Model model) {
        Rfq rfq = rfqService.getRfqById(id);
        Company company = companyService.getCompanyByUserId(rfq.getCreateBy().getId());
        if (rfq == null || company == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("rfq", rfq);
        model.addAttribute("company", company);
        return "sale-staff/cus-rfq-details";
    }

    @PostMapping("/submit-rfq/{id}")
    public String submitRfq(
            @PathVariable Long id,
            @RequestParam("deadlineSolution") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadlineSolution,
            RedirectAttributes redirectAttributes,
            Principal principal) {
        System.err.println("=== Bắt đầu xử lý submitRfq ===");
        System.err.println("ID RFQ: " + id);
        System.err.println("Deadline Solution: " + deadlineSolution);

        userUtils.getOptionalUser(redirectAttributes);

        String emailOrPhone = principal.getName();
        System.err.println("Email/Phone người dùng: " + emailOrPhone);

        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        if (optionalUser.isEmpty()) {
            System.err.println("Không tìm thấy người dùng với email/phone: " + emailOrPhone);
            redirectAttributes.addFlashAttribute("error", "Tài khoản đang dùng không còn tồn tại.");
            return "redirect:/sale-staff/cus-rfq-details/" + id;
        }

        User currentUser = optionalUser.get();
        System.err.println("Người dùng hiện tại: " + currentUser.getUsername() + " (ID: " + currentUser.getId() + ")");

        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null) {
            System.err.println("Không tìm thấy RFQ với ID: " + id);
            redirectAttributes.addFlashAttribute("error", "RFQ không tồn tại.");
            return "redirect:/sale-staff/cus-rfq-details/" + id;
        }
        System.err.println("RFQ tìm thấy: " + rfq.getId() + ", Trạng thái gửi: " + rfq.getIsApproved());

        Company company = companyService.getCompanyByUserId(rfq.getCreateBy().getId());
        System.err.println("Công ty của RFQ: " + (company != null ? company.getName() : "Không tìm thấy công ty"));

        try {
            Rfq submittedRfq = rfqService.submitRfq(id, currentUser.getId(), deadlineSolution);
            System.err.println("Gửi RFQ thành công. RFQ sau khi gửi: " + submittedRfq.getId() + ", Trạng thái gửi: " + submittedRfq.getIsApproved());

            Company currentCompany = companyService.getCompanyByUserId(submittedRfq.getCreateBy().getId());
            System.err.println("Công ty sau khi gửi RFQ: " + (currentCompany != null ? currentCompany.getName() : "Không tìm thấy công ty"));

            redirectAttributes.addFlashAttribute("success", "Gửi yêu cầu báo giá thành công!");
            System.err.println("Flash attribute 'success' được đặt: " + redirectAttributes.getFlashAttributes().get("success"));
            redirectAttributes.addFlashAttribute("rfq", submittedRfq);
            redirectAttributes.addFlashAttribute("company", currentCompany);
        } catch (RuntimeException e) {
            System.err.println("Lỗi RuntimeException khi gửi RFQ: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("rfq", rfq);
            redirectAttributes.addFlashAttribute("company", company);
        } catch (Exception e) {
            System.err.println("Lỗi bất ngờ khi gửi RFQ: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Unexpected Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("rfq", rfq);
            redirectAttributes.addFlashAttribute("company", company);
        }

        System.err.println("=== Kết thúc xử lý submitRfq ===");
        return "redirect:/sale-staff/cus-rfq-details/" + id;
    }

    private static class PageImplWrapper<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImplWrapper(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}