package fpt.g36.gapms.controller.technical;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.SolutionDTO;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.services.*;
import fpt.g36.gapms.utils.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/technical")
public class SaleApprovedRfqController {
    @Autowired
    private final RfqService rfqService;
    @Autowired
    private final UserUtils userUtils;
    @Autowired
    private final UserService userService;
    @Autowired
    private final SolutionService solutionService;
    @Autowired
    private QuotationService quotationService;
    @Autowired
    private MailService mailService;

    public SaleApprovedRfqController(RfqService rfqService, UserUtils userUtils, UserService userService, SolutionService solutionService) {
        this.rfqService = rfqService;
        this.userUtils = userUtils;
        this.userService = userService;
        this.solutionService = solutionService;
    }

    @GetMapping("/view-all-rfq")
    public String getApprovedRfqsViewList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "all-approved-no-solution") String activeTab,
            @RequestParam(required = false) String previousTab,
            Model model,
            Principal principal
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            page = Math.max(0, page);
            size = Math.max(1, size);
            Pageable pageable = PageRequest.of(page, size);

            Page<Rfq> allApprovedNoSolutionPage;
            Page<Rfq> withSolutionPage;
            Page<Rfq> searchResultPage = null;

            // Xử lý tìm kiếm theo ID
            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    try {
                        // Tìm RFQ theo ID
                        Rfq rfq = rfqService.getRfqById(searchId);
                        if (rfq.getIsSent() != BaseEnum.APPROVED) {
                            throw new RuntimeException("RFQ không có trạng thái APPROVED.");
                        }

                        // Tìm trong tab 1: Tất cả RFQ chưa có giải pháp
                        if (rfq.getSolution() == null) {
                            allApprovedNoSolutionPage = new PageImplWrapper<>(Collections.singletonList(rfq), pageable, 1);
                            withSolutionPage = rfqService.getApprovedRfqsWithSolution(pageable);
                            activeTab = "all-approved-no-solution";
                            model.addAttribute("previousTab", activeTab);
                        }
                        // Tìm trong tab 2: Đã có giải pháp
                        else {
                            allApprovedNoSolutionPage = rfqService.getApprovedRfqsWithoutSolution(pageable);
                            withSolutionPage = new PageImplWrapper<>(Collections.singletonList(rfq), pageable, 1);
                            activeTab = "with-solution";
                            model.addAttribute("previousTab", activeTab);
                        }
                        searchResultPage = new PageImplWrapper<>(Collections.singletonList(rfq), pageable, 1);
                    } catch (RuntimeException e) {
                        // Không tìm thấy RFQ hoặc không phù hợp
                        allApprovedNoSolutionPage = rfqService.getApprovedRfqsWithoutSolution(pageable);
                        withSolutionPage = rfqService.getApprovedRfqsWithSolution(pageable);
                        searchResultPage = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                        model.addAttribute("error", "Không tìm thấy RFQ với ID: " + searchId);
                        activeTab = "search-results";
                        model.addAttribute("previousTab", activeTab);
                    }
                } catch (NumberFormatException e) {
                    // ID không hợp lệ
                    model.addAttribute("error", "Mã RFQ phải là số.");
                    allApprovedNoSolutionPage = rfqService.getApprovedRfqsWithoutSolution(pageable);
                    withSolutionPage = rfqService.getApprovedRfqsWithSolution(pageable);
                    searchResultPage = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                    activeTab = (previousTab != null && !previousTab.isEmpty()) ? previousTab : "all-approved-no-solution";
                    model.addAttribute("previousTab", activeTab);
                }
            } else {
                // Nếu không tìm kiếm, lấy dữ liệu cho các tab
                allApprovedNoSolutionPage = rfqService.getApprovedRfqsWithoutSolution(pageable);
                withSolutionPage = rfqService.getApprovedRfqsWithSolution(pageable);
                model.addAttribute("previousTab", activeTab);
            }

            // Thêm dữ liệu vào model cho các tab
            model.addAttribute("allApprovedNoSolution", allApprovedNoSolutionPage.getContent());
            model.addAttribute("allApprovedNoSolutionPage", allApprovedNoSolutionPage);
            model.addAttribute("withSolution", withSolutionPage.getContent());
            model.addAttribute("withSolutionPage", withSolutionPage);
            if (searchResultPage != null) {
                model.addAttribute("searchResults", searchResultPage.getContent());
                model.addAttribute("searchResultPage", searchResultPage);
            }
            model.addAttribute("search", search);
            model.addAttribute("activeTab", activeTab);

            return "technical/view-all-rfq";
        }
        return "redirect:/login";
    }

    @GetMapping("/rfq-details/{id}")
    public String showRfqDetails(@PathVariable Long id, Model model) {
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("rfq", rfq);
        return "technical/rfq-details";
    }

    @PostMapping("/submit-solution/{id}")
    public String submitSolution(@PathVariable Long id, @Valid @ModelAttribute("solution") SolutionDTO solutionDTO, BindingResult result, Principal principal, Model model) {
        userUtils.getOptionalUser(model);

        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Tài khoản đang dùng không còn tồn tại.");
            return "technical/rfq-details";
        }

        User currentUser = optionalUser.get();
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null) {
            model.addAttribute("error", "RFQ không tồn tại.");
            model.addAttribute("rfq", null);
            return "technical/rfq-details";
        }

        model.addAttribute("rfq", rfq);

        if (result.hasErrors()) {
            model.addAttribute("validationErrors", result.getAllErrors());
            return "technical/rfq-details";
        }

        try {
            Solution solution = solutionService.addSolution(id, currentUser.getId(), solutionDTO);
            Rfq updatedRfq = rfqService.getRfqById(id);
            model.addAttribute("rfq", updatedRfq);
            model.addAttribute("success", "Tạo Solution thành công!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Unexpected Error: " + e.getMessage());
        }

        return "technical/rfq-details";
    }

    @GetMapping("/update-solution/{id}")
    public String showUpdateSolutionForm(@PathVariable Long id, Model model) {
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null || rfq.getSolution() == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("rfq", rfq);
        model.addAttribute("solution", rfq.getSolution());
        return "technical/rfq-details";
    }

    @GetMapping("/submit-solution-final/{id}")
    public String showSubmitSolutionForm(@PathVariable Long id, Model model) {
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null || rfq.getSolution() == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("rfq", rfq);
        model.addAttribute("solution", rfq.getSolution());
        return "technical/rfq-details";
    }

    @PostMapping("/update-solution/{id}")
    public String updateSolution(@PathVariable Long id, @Valid @ModelAttribute("solution") SolutionDTO solutionDTO, BindingResult result, Model model) {
        userUtils.getOptionalUser(model);

        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null || rfq.getSolution() == null) {
            model.addAttribute("error", "RFQ hoặc Solution không tồn tại.");
            return "technical/rfq-details";
        }

        model.addAttribute("rfq", rfq);

        if (result.hasErrors()) {
            model.addAttribute("validationErrors", result.getAllErrors());
            return "technical/rfq-details";
        }

        try {
            Solution updatedSolution = solutionService.updateSolution(rfq.getSolution().getId(), solutionDTO);
            Rfq updatedRfq = rfqService.getRfqById(id);
            model.addAttribute("rfq", updatedRfq);
            model.addAttribute("success", "Cập nhật Solution thành công!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Unexpected Error: " + e.getMessage());
        }

        return "technical/rfq-details";
    }

    @PostMapping("/submit-solution-final/{id}")
    public String submitSolutionFinal(@PathVariable Long id, Model model) {
        userUtils.getOptionalUser(model);

        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null || rfq.getSolution() == null) {
            model.addAttribute("error", "RFQ hoặc Solution không tồn tại.");
            return "technical/rfq-details";
        }

        model.addAttribute("rfq", rfq);

        try {
            solutionService.submitSolution(rfq.getSolution().getId());
            // Lấy lại Rfq sau khi submit để đảm bảo dữ liệu mới nhất
            Rfq updatedRfq = rfqService.getRfqById(id);

            quotationService.createQuotationByRfqId(id);
            Long quotationId = quotationService.getQuotationIdByRfqId(id);
            Optional<User> customer = userService.findUsersByRfqId(id);

            /*mailService.sendQuotationEmail(customer.get().getEmail(), customer.get().getUsername(), getuotationId);*/

            model.addAttribute("rfq", updatedRfq); // Cập nhật model với dữ liệu mới
            model.addAttribute("success", "Solution và Quotation đã được gửi thành công!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "technical/rfq-details";
    }

    private static class PageImplWrapper<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImplWrapper(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}