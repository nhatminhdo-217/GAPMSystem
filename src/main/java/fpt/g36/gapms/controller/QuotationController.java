package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.quotation.QuotationInfoDTO;
import fpt.g36.gapms.services.QuotationService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/quotation")
public class QuotationController {

    private final QuotationService quotationService;
    private final UserUtils userUtils;

    public QuotationController(QuotationService quotationService, UserUtils userUtils) {
        this.quotationService = quotationService;
        this.userUtils = userUtils;
    }

    @GetMapping("/detail/{id}")
    public String getQuotationDetail(@PathVariable("id") int id, Model model) {

        QuotationInfoDTO quotation_detail = quotationService.getQuotationInfo(id);

        userUtils.getOptionalUser(model);

        model.addAttribute("quotation_detail", quotation_detail);

        return "quotation/quotation_detail";
    }

    @PostMapping("/detail/{id}")
    public String postQuotationDetail(@PathVariable("id") int id, Model model) {


        return "quotation/quotation_detail";
    }

}
