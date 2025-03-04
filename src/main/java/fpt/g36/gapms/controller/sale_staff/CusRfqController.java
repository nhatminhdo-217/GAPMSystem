package fpt.g36.gapms.controller.sale_staff;

import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.Solution;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.RfqService;
import fpt.g36.gapms.services.SolutionService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
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

    public CusRfqController(RfqService rfqService, UserUtils userUtils, UserService userService) {
        this.rfqService = rfqService;
        this.userUtils = userUtils;
        this.userService = userService;
    }

    @GetMapping("/view-all-cus-rfq")
    public String getSolutionViewList(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            List<Rfq> rfq = rfqService.getAllRfq();
            model.addAttribute("rfqs", rfq);
        }
        userUtils.getOptionalUser(model);
        return "/sale-staff/view-all-cus-rfq";
    }

    @GetMapping("/cus-rfq-details/{id}")
    public String getSolutionDetailsView(@PathVariable Long id, Model model) {
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("rfq", rfq);
        return "/sale-staff/cus-rfq-details";
    }

    @PostMapping("/submit-rfq/{id}")
    public String submitRfq(@PathVariable Long id, Model model, Principal principal) {
        userUtils.getOptionalUser(model);

        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Tài khoản đang dùng không còn tồn tại.");
            return "/sale-staff/cus-rfq-details";
        }

        User currentUser = optionalUser.get();
        Rfq rfq = rfqService.getRfqById(id);
        if (rfq == null) {
            model.addAttribute("error", "RFQ không tồn tại.");
            model.addAttribute("rfq", null);
            return "/sale-staff/cus-rfq-details";
        }

        model.addAttribute("rfq", rfq);

        try {
            Rfq submittedRfq = rfqService.submitRfq(id, currentUser.getId());
            model.addAttribute("rfq", submittedRfq); // Cập nhật RFQ sau khi gửi
            model.addAttribute("success", "Gửi RFQ thành công!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Unexpected Error: " + e.getMessage());
        }

        return "/sale-staff/cus-rfq-details";
    }
}
