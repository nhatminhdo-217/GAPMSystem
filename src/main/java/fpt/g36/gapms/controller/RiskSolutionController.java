package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.quotation.QuotationInforCustomerDTO;
import fpt.g36.gapms.models.entities.RiskSolution;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.RiskSolutionService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/risk-solution")
public class RiskSolutionController {

    @Autowired
    private RiskSolutionService riskSolutionService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;
    @GetMapping("/technical/view-list")
    public String getViewList(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size) {


        Pageable pageable = PageRequest.of(page, size);
        Page<RiskSolution> riskSolutions = riskSolutionService.getAllRiskSolution(pageable);
        model.addAttribute("riskSolutions", riskSolutions.getContent());
        model.addAttribute("currentPage", riskSolutions.getNumber()); // Trang hiện tại
        model.addAttribute("totalPages", riskSolutions.getTotalPages());
        System.err.println("totalPages" + riskSolutions.getTotalPages());// Tổng số trang
        model.addAttribute("totalItems", riskSolutions.getTotalElements()); // Tổng số bản ghi
        model.addAttribute("pageSize", size); // Số bản ghi mỗi trang

        userUtils.getOptionalUser(model);

        return "risk-solution/view-list-technical-risk-solution";
    }

    @GetMapping("/technical/detail/{id}")
     public String getDetail(@PathVariable("id") Long riskSolutionId, Model model) {
        RiskSolution riskSolution = riskSolutionService.getRiskSolutionById(riskSolutionId);
        model.addAttribute("riskSolution", riskSolution);
        userUtils.getOptionalUser(model);
        return "risk-solution/view-detail-technical-risk-solution";
    }

    @PostMapping("/technical/save-solution/{id}")
    public String saveRiskSolution(@PathVariable("id") Long riskSolutionId, @ModelAttribute("riskSolution") RiskSolution riskSolution, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        RiskSolution riskSolution_save = riskSolutionService.saveRiskSolution(riskSolutionId, riskSolution, optionalUser.get());
        redirectAttributes.addFlashAttribute("save_success", "Đã tạo phiếu yêu cầu cấp sợi thành công");
        return "redirect:/risk-solution/technical/detail/" + riskSolution_save.getId();
    }


    @PostMapping("/technical/risk-solution-approved/{id}")
    public String approveSolutionEasy(@PathVariable("id") Long rsId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        RiskSolution riskSolution = riskSolutionService.approveEasyRiskSolution(rsId, optionalUser.get());
        redirectAttributes.addFlashAttribute("approve_easy", "Đã xác nhận làm lại công đoạn");
        return "redirect:/risk-solution/technical/detail/" +riskSolution.getId();
    }


    /*-------------------------Production Manager--------------------------------------------*/

    @GetMapping("/production-manager/view-list")
    public String getViewListPm(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size) {


        Pageable pageable = PageRequest.of(page, size);
        Page<RiskSolution> riskSolutions = riskSolutionService.getAllRiskSolutionManager(pageable);
        model.addAttribute("riskSolutions", riskSolutions.getContent());
        model.addAttribute("currentPage", riskSolutions.getNumber()); // Trang hiện tại
        model.addAttribute("totalPages", riskSolutions.getTotalPages());
        System.err.println("totalPages" + riskSolutions.getTotalPages());// Tổng số trang
        model.addAttribute("totalItems", riskSolutions.getTotalElements()); // Tổng số bản ghi
        model.addAttribute("pageSize", size); // Số bản ghi mỗi trang

        userUtils.getOptionalUser(model);

        return "risk-solution/view-list-product-manager-risk-solution";
    }

    @GetMapping("/production-manage/detail/{id}")
    public String getDetailPm(@PathVariable("id") Long riskSolutionId, Model model) {
        RiskSolution riskSolution = riskSolutionService.getRiskSolutionById(riskSolutionId);
        model.addAttribute("riskSolution", riskSolution);
        userUtils.getOptionalUser(model);
        return "risk-solution/view-detail-product-manager-risk-solution";
    }

    @GetMapping("/production-manage/risk-solution-approved/{id}")
    public String approveSolution(@PathVariable("id") Long rsId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        RiskSolution riskSolution = riskSolutionService.approveRiskSolution(rsId, optionalUser.get());
        redirectAttributes.addFlashAttribute("approved", "Phiếu cấp sợi đã được duyệt");
        return "redirect:/risk-solution/production-manage/detail/" +riskSolution.getId();
    }
}
