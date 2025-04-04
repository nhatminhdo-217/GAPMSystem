package fpt.g36.gapms.controller.sale_staff;

import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.Solution;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.CompanyService;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
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
    public String getSolutionViewList(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            List<Solution> solutions = solutionService.getAllSentedAndApproveByUserIDSolutions(optionalUser.get().getId());
            model.addAttribute("solution", solutions);
        }
        userUtils.getOptionalUser(model);
        return "/sale-staff/view-all-rfq-solution";
    }

    @GetMapping("/rfq-solution-details/{id}")
    public String getSolutionDetailsView(@PathVariable Long id, Model model) {
        Solution solution = solutionService.getSolutionById(id);
        Company company = companyService.getCompanyByUserId(solution.getCreateBy().getId());
        if (solution == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("solution", solution);
        model.addAttribute("company", company);
        return "/sale-staff/rfq-solution-details";
    }
}
