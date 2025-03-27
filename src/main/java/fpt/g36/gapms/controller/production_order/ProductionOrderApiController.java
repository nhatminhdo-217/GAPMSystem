package fpt.g36.gapms.controller.production_order;

import fpt.g36.gapms.models.dto.production_order.ProductionOrderDetailDTO;
import fpt.g36.gapms.services.ProductionOrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production-order/api")
public class ProductionOrderApiController {

    private final ProductionOrderService productionOrderService;

    public ProductionOrderApiController(ProductionOrderService productionOrderService) {
        this.productionOrderService = productionOrderService;
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public ProductionOrderDetailDTO getProductionOrderDetail(@PathVariable("id") Long id) {
        return productionOrderService.findDetailById(id);
    }
}
