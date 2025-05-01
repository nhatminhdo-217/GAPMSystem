package fpt.g36.gapms.controller.sale_staff;

import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.Solution;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.CompanyService;
import fpt.g36.gapms.services.RfqService;
import fpt.g36.gapms.services.SolutionService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sale-staff")
public class RfqSolutionController {
    @Autowired
    private final RfqService rfqService;
    @Autowired
    private final UserUtils userUtils;
    @Autowired
    private final UserService userService;
    @Autowired
    private final SolutionService solutionService;
    @Autowired
    private final CompanyService companyService;

    public RfqSolutionController(RfqService rfqService, UserUtils userUtils, UserService userService, SolutionService solutionService, CompanyService companyService) {
        this.rfqService = rfqService;
        this.userUtils = userUtils;
        this.userService = userService;
        this.solutionService = solutionService;
        this.companyService = companyService;
    }

    @GetMapping("/view-all-rfq-solution")
    public String getSolutionViewList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "user-solutions-content") String activeTab,
            @RequestParam(required = false) String previousTab,
            Model model,
            Principal principal
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = principal.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (!optionalUser.isPresent()) {
                System.err.println("Không tìm thấy User với email/phone: " + emailOrPhone + ", chuyển hướng đến trang login.");
                return "redirect:/login";
            }

            User currentUser = optionalUser.get();
            Long userId = currentUser.getId();
            Pageable pageable = PageRequest.of(page, size);

            Page<Solution> userSolutionsPage;
            Page<Solution> allSentedSolutionsPage;

            // Xử lý tìm kiếm theo ID
            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    try {
                        // Tìm kiếm trong tất cả solution đã gửi
                        Solution solution = solutionService.getSolutionById(searchId);
                        // Kiểm tra nếu solution không có trạng thái SENT thì không hiển thị
                        if (solution.getIsSent() != SendEnum.SENT) {
                            throw new RuntimeException("Solution không có trạng thái SENT.");
                        }
                        userSolutionsPage = solutionService.getAllSentedAndApproveByUserIDSolutions(userId, pageable); // Giữ nguyên dữ liệu tab 1
                        allSentedSolutionsPage = new PageImplWrapper<>(Collections.singletonList(solution), pageable, 1);
                        activeTab = "all-sented-solutions-content";
                        model.addAttribute("previousTab", activeTab);
                    } catch (RuntimeException e) {
                        // Không tìm thấy solution hoặc solution không có trạng thái SENT
                        userSolutionsPage = solutionService.getAllSentedAndApproveByUserIDSolutions(userId, pageable);
                        allSentedSolutionsPage = solutionService.getAllSentedSolutions(pageable);
                        model.addAttribute("error", "Không tìm thấy Solution với ID: " + searchId);
                        activeTab = (previousTab != null && !previousTab.isEmpty()) ? previousTab : "user-solutions-content";
                        model.addAttribute("previousTab", activeTab);
                    }
                } catch (NumberFormatException e) {
                    // Xử lý trường hợp ID không hợp lệ
                    model.addAttribute("error", "Mã Solution phải là số.");
                    userSolutionsPage = solutionService.getAllSentedAndApproveByUserIDSolutions(userId, pageable);
                    allSentedSolutionsPage = solutionService.getAllSentedSolutions(pageable);
                    activeTab = (previousTab != null && !previousTab.isEmpty()) ? previousTab : "user-solutions-content";
                    model.addAttribute("previousTab", activeTab);
                }
            } else {
                // Nếu không có tìm kiếm, lấy dữ liệu đầy đủ cho tất cả các tab
                userSolutionsPage = solutionService.getAllSentedAndApproveByUserIDSolutions(userId, pageable);
                allSentedSolutionsPage = solutionService.getAllSentedSolutions(pageable);
                model.addAttribute("previousTab", activeTab);
            }
            //
            model.addAttribute("userSolutions", userSolutionsPage.getContent());
            model.addAttribute("userSolutionsPage", userSolutionsPage);
            model.addAttribute("allSentedSolutions", allSentedSolutionsPage.getContent());
            model.addAttribute("allSentedSolutionsPage", allSentedSolutionsPage);
            model.addAttribute("search", search);
            model.addAttribute("activeTab", activeTab);

            return "sale-staff/view-all-rfq-solution";
        }
        return "redirect:/login";
    }

    @GetMapping("/rfq-solution-details/{id}")
    public String getSolutionDetailsView(@PathVariable Long id, Model model) {
        Solution solution = solutionService.getSolutionById(id);
        Company company = companyService.getCompanyByUserId(solution.getRfq().getCreateBy().getId());
        if (solution == null) {
            return "redirect:/error";
        }
        if (company == null) {
            System.err.println("Company not found");
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("solution", solution);
        model.addAttribute("company", company);
        return "sale-staff/rfq-solution-details";
    }

    private static class PageImplWrapper<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImplWrapper(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}
