package fpt.g36.gapms.controller.production_order;

import fpt.g36.gapms.models.dto.production_order.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDetailDTO;
import fpt.g36.gapms.services.ProductionOrderService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/production-order")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;
    private final UserUtils userUtils;

    public ProductionOrderController(ProductionOrderService productionOrderService, UserUtils userUtils) {
        this.productionOrderService = productionOrderService;
        this.userUtils = userUtils;
    }

    @GetMapping("/list")
    public String listProductionOrder(Model model) {
        return findPaginated(1, "create_at", "asc", model);
    }

    @GetMapping("/list/page/{page}")
    public String findPaginated(
            @PathVariable("page") Integer page,
            @RequestParam("sortField") String sortField,
            @RequestParam("sortDir") String sortDir,
            Model model
    ) {

        userUtils.getOptionalUser(model);

        int pageSize = 5;

        Page<ProductionOrderDTO> pageData = productionOrderService.findPaginated(page, pageSize, sortField, sortDir);

        List<ProductionOrderDTO> productionOrderList = pageData.getContent();

        model.addAttribute("productionOrderList", productionOrderList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("totalItems", pageData.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "production_order/list_production_order";
    }

    @GetMapping("/detail/{id}")
    public String detailProductionOrder(@PathVariable("id") Long id, Model model) {

        userUtils.getOptionalUser(model);

        ProductionOrderDTO productionOrder = productionOrderService.findById(id);
        List<ProductionOrderDetailDTO> productionOrderDetailList = productionOrderService.findDetailByProductionOrderId(id);

        model.addAttribute("productionOrder", productionOrder);
        model.addAttribute("productionOrderDetailList", productionOrderDetailList);


        return "production_order/detail_production_order";
    }
}
