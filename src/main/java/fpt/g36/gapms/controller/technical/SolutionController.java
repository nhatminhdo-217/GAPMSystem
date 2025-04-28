package fpt.g36.gapms.controller.technical;

import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.entities.Solution;
import fpt.g36.gapms.models.entities.User;
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
@RequestMapping("/technical")
public class SolutionController {
    @Autowired
    private final RfqService rfqService;
    @Autowired
    private final UserUtils userUtils;
    @Autowired
    private final UserService userService;
    @Autowired
    private final SolutionService solutionService;

    public SolutionController(RfqService rfqService, UserUtils userUtils, UserService userService, SolutionService solutionService) {
        this.rfqService = rfqService;
        this.userUtils = userUtils;
        this.userService = userService;
        this.solutionService = solutionService;
    }

    @GetMapping("/view-all-solution")
    public String getSolutionViewList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "not-sent-solutions-content") String activeTab,
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

            Page<Solution> notSentSolutionsPage;
            Page<Solution> sentSolutionsPage;

            // Xử lý tìm kiếm theo ID
            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    try {
                        // Tìm kiếm Solution theo ID và createdBy
                        Optional<Solution> solutionOpt = solutionService.findSolutionByIdAndCreateById(searchId, userId);
                        if (!solutionOpt.isPresent()) {
                            throw new RuntimeException("Không tìm thấy Solution với ID: " + searchId);
                        }
                        Solution solution = solutionOpt.get();
                        // Hiển thị kết quả tìm kiếm trong tab tương ứng, tab còn lại rỗng
                        if (solution.getIsSent() == SendEnum.NOT_SENT) {
                            notSentSolutionsPage = new PageImplWrapper<>(
                                    Collections.singletonList(solution),
                                    pageable,
                                    1
                            );
                            sentSolutionsPage = new PageImplWrapper<>(
                                    Collections.emptyList(),
                                    pageable,
                                    0
                            );
                            activeTab = "not-sent-solutions-content";
                        } else {
                            sentSolutionsPage = new PageImplWrapper<>(
                                    Collections.singletonList(solution),
                                    pageable,
                                    1
                            );
                            notSentSolutionsPage = new PageImplWrapper<>(
                                    Collections.emptyList(),
                                    pageable,
                                    0
                            );
                            activeTab = "sent-solutions-content";
                        }
                        model.addAttribute("previousTab", activeTab);
                    } catch (RuntimeException e) {
                        // Không tìm thấy Solution
                        notSentSolutionsPage = new PageImplWrapper<>(
                                Collections.emptyList(),
                                pageable,
                                0
                        );
                        sentSolutionsPage = new PageImplWrapper<>(
                                Collections.emptyList(),
                                pageable,
                                0
                        );
                        model.addAttribute("error", "Không tìm thấy Solution với ID: " + searchId);
                        activeTab = (previousTab != null && !previousTab.isEmpty()) ? previousTab : "not-sent-solutions-content";
                        model.addAttribute("previousTab", activeTab);
                    }
                } catch (NumberFormatException e) {
                    // ID không hợp lệ
                    model.addAttribute("error", "Mã Solution phải là số.");
                    notSentSolutionsPage = new PageImplWrapper<>(
                            Collections.emptyList(),
                            pageable,
                            0
                    );
                    sentSolutionsPage = new PageImplWrapper<>(
                            Collections.emptyList(),
                            pageable,
                            0
                    );
                    activeTab = (previousTab != null && !previousTab.isEmpty()) ? previousTab : "not-sent-solutions-content";
                    model.addAttribute("previousTab", activeTab);
                }
            } else {
                // Nếu không có tìm kiếm, lấy dữ liệu đầy đủ cho cả hai tab
                notSentSolutionsPage = solutionService.getSolutionsByCreateByIdAndIsSentOrderByRfqDeadline(userId, SendEnum.NOT_SENT, pageable);
                sentSolutionsPage = solutionService.getSolutionsByCreateByIdAndIsSentOrderByRfqDeadline(userId, SendEnum.SENT, pageable);
                model.addAttribute("previousTab", activeTab);
            }

            // Thêm dữ liệu vào model
            model.addAttribute("notSentSolutions", notSentSolutionsPage.getContent());
            model.addAttribute("notSentSolutionsPage", notSentSolutionsPage);
            model.addAttribute("sentSolutions", sentSolutionsPage.getContent());
            model.addAttribute("sentSolutionsPage", sentSolutionsPage);
            model.addAttribute("search", search);
            model.addAttribute("activeTab", activeTab);

            return "technical/view-all-solution";
        }
        return "redirect:/login";
    }

    @GetMapping("/solution-details/{id}")
    public String getSolutionDetailsView(@PathVariable Long id, Model model) {
        Solution solution = solutionService.getSolutionById(id);
        if (solution == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("solution", solution);
        return "technical/solution-details";
    }

    // Định nghĩa lớp PageImplWrapper
    private static class PageImplWrapper<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImplWrapper(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}