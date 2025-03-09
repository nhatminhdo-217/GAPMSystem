package fpt.g36.gapms.controller.technical;

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
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
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
    public String getSolutionViewList(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            List<Solution> solutions = solutionService.getSolutionsByCreabyID(optionalUser.get().getId());
            model.addAttribute("solution", solutions);
        }
        userUtils.getOptionalUser(model);
        return "technical/view-all-solution";
    }

    @GetMapping("/solution-details/{id}")
    public String getSolutionDetailsView(@PathVariable Long id, Model model) {
        Solution solution = solutionService.getSolutionById(id);
        if (solution == null) {
            return "redirect:/error";
        }
        userUtils.getOptionalUser(model);
        model.addAttribute("solution", solution);
        return "/technical/solution-details";
    }
}
