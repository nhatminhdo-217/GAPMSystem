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
public class CusRfqController {
    @Autowired
    private final RfqService rfqService;
    @Autowired
    private final UserUtils userUtils;
    @Autowired
    private final UserService userService;
    @Autowired
    private final SolutionService solutionService;

    public CusRfqController(RfqService rfqService, UserUtils userUtils, UserService userService, SolutionService solutionService) {
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
        //
        Rfq rfq = rfqService.getRfqById(id);
        //
        if (rfq == null) {
            return "redirect:/error";
        }
        //
        userUtils.getOptionalUser(model);
        model.addAttribute("rfq", rfq);
        //
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

        // Kiểm tra và lấy RFQ
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null) {
            model.addAttribute("error", "RFQ không tồn tại.");
            model.addAttribute("rfq", null);
            return "/technical/rfq-details";
        }

        model.addAttribute("rfq", rfq);

        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            model.addAttribute("validationErrors", result.getAllErrors());
            return "/technical/rfq-details";
        }

        try {
            // Gọi service để lưu Solution
            Solution solution = solutionService.addSolution(id, currentUser.getId(), solutionDTO);
            System.err.println("Solution saved with ID: " + solution.getId() + ", RFQ ID: " + solution.getRfq().getId());
            // Tải lại RFQ từ database để lấy thông tin mới nhất (bao gồm Solution vừa lưu)
            Rfq updatedRfq = rfqService.getRfqById(id);
            System.err.println("Updated RFQ: " + updatedRfq + ", Solution: " + updatedRfq.getSolutions()); // Sử dụng getSolution() nếu đúng getter
            model.addAttribute("rfq", updatedRfq); // Cập nhật model với RFQ mới
            model.addAttribute("success", "Tạo Solution thành công!");
        } catch (RuntimeException e) {
            System.err.println("Runtime Error: " + e.getMessage());
            model.addAttribute("error", "Runtime Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            model.addAttribute("error", "Unexpected Error: " + e.getMessage());
        }

        return "/technical/rfq-details";
    }
}
