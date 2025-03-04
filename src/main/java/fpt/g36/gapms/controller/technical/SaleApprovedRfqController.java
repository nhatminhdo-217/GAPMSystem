package fpt.g36.gapms.controller.technical;

import fpt.g36.gapms.models.dto.SolutionDTO;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.services.RfqService;
import fpt.g36.gapms.services.SolutionService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.utils.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    public SaleApprovedRfqController(RfqService rfqService, UserUtils userUtils, UserService userService, SolutionService solutionService) {
        this.rfqService = rfqService;
        this.userUtils = userUtils;
        this.userService = userService;
        this.solutionService = solutionService;
    }

    @GetMapping("/view-all-rfq")
    public String getApprovedRfqsViewList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            List<Rfq> rfqs = rfqService.getAllApprovedRfqs();
            model.addAttribute("rfqs", rfqs);
        }
        userUtils.getOptionalUser(model);
        return "technical/view-all-rfq";
    }

    @GetMapping("/rfq-details/{id}")
    public String showRfqDetails(@PathVariable Long id, Model model) {
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("rfq", rfq);
        return "/technical/rfq-details";
    }

    @PostMapping("/submit-solution/{id}")
    public String submitSolution(@PathVariable Long id, @Valid @ModelAttribute("solution") SolutionDTO solutionDTO, BindingResult result, Principal principal, Model model) {
        userUtils.getOptionalUser(model);

        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Tài khoản đang dùng không còn tồn tại.");
            return "/technical/rfq-details";
        }

        User currentUser = optionalUser.get();
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null) {
            model.addAttribute("error", "RFQ không tồn tại.");
            model.addAttribute("rfq", null);
            return "/technical/rfq-details";
        }

        model.addAttribute("rfq", rfq);

        if (result.hasErrors()) {
            model.addAttribute("validationErrors", result.getAllErrors());
            return "/technical/rfq-details";
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

        return "/technical/rfq-details";
    }

    @GetMapping("/update-solution/{id}")
    public String showUpdateSolutionForm(@PathVariable Long id, Model model) {
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null || rfq.getSolutions() == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("rfq", rfq);
        model.addAttribute("solution", rfq.getSolutions());
        return "/technical/rfq-details";
    }

    @GetMapping("/submit-solution-final/{id}")
    public String showSubmitSolutionForm(@PathVariable Long id, Model model) {
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null || rfq.getSolutions() == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("rfq", rfq);
        model.addAttribute("solution", rfq.getSolutions());
        return "/technical/rfq-details";
    }

    @PostMapping("/update-solution/{id}")
    public String updateSolution(@PathVariable Long id, @Valid @ModelAttribute("solution") SolutionDTO solutionDTO, BindingResult result, Model model) {
        userUtils.getOptionalUser(model);

        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null || rfq.getSolutions() == null) {
            model.addAttribute("error", "RFQ hoặc Solution không tồn tại.");
            return "/technical/rfq-details";
        }

        model.addAttribute("rfq", rfq);

        if (result.hasErrors()) {
            model.addAttribute("validationErrors", result.getAllErrors());
            return "/technical/rfq-details";
        }

        try {
            Solution updatedSolution = solutionService.updateSolution(rfq.getSolutions().getId(), solutionDTO);
            Rfq updatedRfq = rfqService.getRfqById(id);
            model.addAttribute("rfq", updatedRfq);
            model.addAttribute("success", "Cập nhật Solution thành công!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Unexpected Error: " + e.getMessage());
        }

        return "/technical/rfq-details";
    }

    @PostMapping("/submit-solution-final/{id}")
    public String submitSolutionFinal(@PathVariable Long id, Model model) {
        userUtils.getOptionalUser(model);

        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null || rfq.getSolutions() == null) {
            model.addAttribute("error", "RFQ hoặc Solution không tồn tại.");
            return "/technical/rfq-details";
        }

        model.addAttribute("rfq", rfq);

        try {
            solutionService.submitSolution(rfq.getSolutions().getId());
            // Lấy lại Rfq sau khi submit để đảm bảo dữ liệu mới nhất
            Rfq updatedRfq = rfqService.getRfqById(id);
            model.addAttribute("rfq", updatedRfq); // Cập nhật model với dữ liệu mới
            model.addAttribute("success", "Solution đã được gửi thành công!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "/technical/rfq-details";
    }
}