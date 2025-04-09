package fpt.g36.gapms.controller.technical;

import fpt.g36.gapms.models.entities.DyeMachine;
import fpt.g36.gapms.models.entities.WindingMachine;
import fpt.g36.gapms.services.MachineService;
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

import java.util.Collections;

@Controller
@RequestMapping("/technical")
public class MachineController {
    private final MachineService machineService;
    private final UserUtils userUtils;

    @Autowired
    public MachineController(MachineService machineService, UserUtils userUtils) {
        this.machineService = machineService;
        this.userUtils = userUtils;
    }

    @GetMapping("/view-all-machine")
    public String viewAllMachines(
            @RequestParam(defaultValue = "0") int pageDye,
            @RequestParam(defaultValue = "0") int pageWinding,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dye") String tab,
            @RequestParam(required = false) String search,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Pageable dyePageable = PageRequest.of(pageDye, size);
            Pageable windingPageable = PageRequest.of(pageWinding, size);

            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    if ("dye".equals(tab)) {
                        try {
                            DyeMachine dyeMachine = machineService.getDyeMachinesById(searchId);
                            Page<DyeMachine> dyePage = new PageImplWrapper<>(Collections.singletonList(dyeMachine), dyePageable, 1);
                            model.addAttribute("dyeMachines", dyePage.getContent());
                            model.addAttribute("dyePage", dyePage);
                        } catch (RuntimeException e) {
                            Page<DyeMachine> dyePage = new PageImplWrapper<>(Collections.emptyList(), dyePageable, 0);
                            model.addAttribute("dyeMachines", Collections.emptyList());
                            model.addAttribute("dyePage", dyePage);
                        }
                        Page<WindingMachine> windingPage = machineService.getAllWindingMachines(windingPageable);
                        model.addAttribute("windingMachines", windingPage.getContent());
                        model.addAttribute("windingPage", windingPage);
                    } else {
                        try {
                            WindingMachine windingMachine = machineService.getWindingMachinesById(searchId);
                            Page<WindingMachine> windingPage = new PageImplWrapper<>(Collections.singletonList(windingMachine), windingPageable, 1);
                            model.addAttribute("windingMachines", windingPage.getContent());
                            model.addAttribute("windingPage", windingPage);
                        } catch (RuntimeException e) {
                            Page<WindingMachine> windingPage = new PageImplWrapper<>(Collections.emptyList(), windingPageable, 0);
                            model.addAttribute("windingMachines", Collections.emptyList());
                            model.addAttribute("windingPage", windingPage);
                        }
                        Page<DyeMachine> dyePage = machineService.getAllDyeMachines(dyePageable);
                        model.addAttribute("dyeMachines", dyePage.getContent());
                        model.addAttribute("dyePage", dyePage);
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("error", "Mã máy phải là số.");
                    Page<DyeMachine> dyePage = machineService.getAllDyeMachines(dyePageable);
                    model.addAttribute("dyeMachines", dyePage.getContent());
                    model.addAttribute("dyePage", dyePage);
                    Page<WindingMachine> windingPage = machineService.getAllWindingMachines(windingPageable);
                    model.addAttribute("windingMachines", windingPage.getContent());
                    model.addAttribute("windingPage", windingPage);
                }
            } else {
                Page<DyeMachine> dyePage = machineService.getAllDyeMachines(dyePageable);
                model.addAttribute("dyeMachines", dyePage.getContent());
                model.addAttribute("dyePage", dyePage);

                Page<WindingMachine> windingPage = machineService.getAllWindingMachines(windingPageable);
                model.addAttribute("windingMachines", windingPage.getContent());
                model.addAttribute("windingPage", windingPage);
            }

            model.addAttribute("tab", tab);
            model.addAttribute("search", search);
            return "technical/view-all-machine";
        }
        return "redirect:/login";
    }

    private static class PageImplWrapper<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImplWrapper(java.util.List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

    @GetMapping("/dye-machine-details/{id}")
    public String viewDyeMachineDetails(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            if (id == null) {
                model.addAttribute("error", "ID máy nhuộm không hợp lệ.");
                return "technical/view-all-machine";
            }
            try {
                DyeMachine dyeMachine = machineService.getDyeMachinesById(id);
                model.addAttribute("dyeMachine", dyeMachine);
                return "technical/dye-machine-details";
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
                return "error";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/winding-machine-details/{id}")
    public String viewWindingMachineDetails(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            if (id == null) {
                model.addAttribute("error", "ID máy cuốn không hợp lệ.");
                return "technical/view-all-machine";
            }
            try {
                WindingMachine windingMachine = machineService.getWindingMachinesById(id);
                model.addAttribute("windingMachine", windingMachine);
                return "technical/winding-machine-details";
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
                return "technical/view-all-machine";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/create-machine")
    public String createMachine(
            @RequestParam("machineType") String machineType,
            @ModelAttribute("dyeMachine") DyeMachine dyeMachine,
            @ModelAttribute("windingMachine") WindingMachine windingMachine,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                if ("dye".equals(machineType)) {
                    if (dyeMachine.getDiameter() == null || dyeMachine.getDiameter() <= 0 ||
                            dyeMachine.getPile() == null || dyeMachine.getPile() <= 0 ||
                            dyeMachine.getConePerPile() == null || dyeMachine.getConePerPile() <= 0 ||
                            dyeMachine.getMaxWeight() == null || dyeMachine.getMaxWeight() <= 0 ||
                            dyeMachine.getCapacity() == null || dyeMachine.getCapacity() <= 0) {
                        throw new IllegalArgumentException("Thông tin máy nhuộm không hợp lệ. Các giá trị phải lớn hơn 0.");
                    }
                    if (dyeMachine.getLittersMax().compareTo(dyeMachine.getLittersMin()) <= 0
                            || dyeMachine.getConeMax().compareTo(dyeMachine.getConeMin()) <= 0) {
                        throw new IllegalArgumentException("Giá trị của litter max phải lớn hơn litters min " +
                                "và giá trị của cone max phải lớn hơn cone min");
                    }
                    ;
                    DyeMachine savedDyeMachine = machineService.addDyeMachine(dyeMachine);
                    model.addAttribute("dyeMachine", savedDyeMachine);
                    model.addAttribute("success", "Tạo máy nhuộm thành công!");
                    System.err.println("Rendering dye-machine-details with success: " + model.containsAttribute("success"));
                    return "technical/dye-machine-details";
                } else if ("winding".equals(machineType)) {
                    if (windingMachine.getMotor_speed() == null || windingMachine.getMotor_speed() <= 0 ||
                            windingMachine.getSpindle() == null || windingMachine.getSpindle() <= 0 ||
                            windingMachine.getCapacity() == null || windingMachine.getCapacity() <= 0) {
                        throw new IllegalArgumentException("Thông tin máy cuốn không hợp lệ. Các giá trị phải lớn hơn 0.");
                    }
                    WindingMachine savedWindingMachine = machineService.addWindingMachine(windingMachine);
                    model.addAttribute("windingMachine", savedWindingMachine);
                    model.addAttribute("success", "Tạo máy cuốn thành công!");
                    System.err.println("Rendering winding-machine-details with success: " + model.containsAttribute("success"));
                    return "technical/winding-machine-details";
                } else {
                    model.addAttribute("error", "Loại máy không hợp lệ.");
                    return "technical/view-all-machine";
                }
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                return "technical/view-all-machine";
            } catch (Exception e) {
                model.addAttribute("error", "Có lỗi xảy ra khi thêm máy: " + e.getMessage());
                return "technical/view-all-machine";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/edit-dye-machine/{id}")
    public String showEditDyeMachineForm(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                DyeMachine dyeMachine = machineService.getDyeMachinesById(id);
                if (dyeMachine.getDyeStage() != null) {
                    model.addAttribute("error", "Máy đã được gán vào stage, không thể chỉnh sửa.");
                    model.addAttribute("dyeMachine", dyeMachine);
                    return "technical/dye-machine-details";
                }
                model.addAttribute("dyeMachine", dyeMachine);
                return "technical/edit-dye-machine";
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
                return "technical/view-all-machine";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/edit-winding-machine/{id}")
    public String showEditWindingMachineForm(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WindingMachine windingMachine = machineService.getWindingMachinesById(id);
                if (windingMachine.getWindingStage() != null) {
                    model.addAttribute("error", "Máy đã được gán vào stage, không thể chỉnh sửa.");
                    model.addAttribute("windingMachine", windingMachine);
                    return "technical/winding-machine-details";
                }
                model.addAttribute("windingMachine", windingMachine);
                return "technical/edit-winding-machine";
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
                return "technical/view-all-machine";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/update-dye-machine")
    public String updateDyeMachine(
            @RequestParam("id") Long id,
            @ModelAttribute("dyeMachine") DyeMachine dyeMachine,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                if (dyeMachine.getDiameter() == null || dyeMachine.getDiameter() <= 0 ||
                        dyeMachine.getPile() == null || dyeMachine.getPile() <= 0 ||
                        dyeMachine.getConePerPile() == null || dyeMachine.getConePerPile() <= 0 ||
                        dyeMachine.getMaxWeight() == null || dyeMachine.getMaxWeight() <= 0 ||
                        dyeMachine.getCapacity() == null || dyeMachine.getCapacity() <= 0) {
                    throw new IllegalArgumentException("Thông tin máy nhuộm không hợp lệ. Các giá trị phải lớn hơn 0.");
                }
                if (dyeMachine.getLittersMax().compareTo(dyeMachine.getLittersMin()) <= 0
                        || dyeMachine.getConeMax().compareTo(dyeMachine.getConeMin()) <= 0) {
                    throw new IllegalArgumentException("Giá trị của litter max phải lớn hơn litters min " +
                            "và giá trị của cone max phải lớn hơn cone min");
                }
                ;
                DyeMachine updatedDyeMachine = machineService.updateDyeMachine(id, dyeMachine);
                model.addAttribute("dyeMachine", updatedDyeMachine);
                model.addAttribute("success", "Cập nhật máy nhuộm thành công!");
                return "technical/dye-machine-details";
            } catch (IllegalStateException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("dyeMachine", machineService.getDyeMachinesById(id));
                return "technical/dye-machine-details";
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("dyeMachine", machineService.getDyeMachinesById(id));
                return "technical/edit-dye-machine";
            } catch (Exception e) {
                model.addAttribute("error", "Có lỗi xảy ra khi cập nhật máy: " + e.getMessage());
                return "technical/view-all-machine";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/update-winding-machine")
    public String updateWindingMachine(
            @RequestParam("id") Long id,
            @ModelAttribute("windingMachine") WindingMachine windingMachine,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                if (windingMachine.getMotor_speed() == null || windingMachine.getMotor_speed() <= 0 ||
                        windingMachine.getSpindle() == null || windingMachine.getSpindle() <= 0 ||
                        windingMachine.getCapacity() == null || windingMachine.getCapacity() <= 0) {
                    throw new IllegalArgumentException("Thông tin máy cuốn không hợp lệ. Các giá trị phải lớn hơn 0.");
                }
                WindingMachine updatedWindingMachine = machineService.updateWindingMachine(id, windingMachine);
                model.addAttribute("windingMachine", updatedWindingMachine);
                model.addAttribute("success", "Cập nhật máy cuốn thành công!");
                return "technical/winding-machine-details";
            } catch (IllegalStateException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("windingMachine", machineService.getWindingMachinesById(id));
                return "technical/winding-machine-details";
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("windingMachine", machineService.getWindingMachinesById(id));
                return "technical/edit-winding-machine";
            } catch (Exception e) {
                model.addAttribute("error", "Có lỗi xảy ra khi cập nhật máy: " + e.getMessage());
                return "technical/view-all-machine";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/dye-machine/{id}/toggle-status")
    public String toggleDyeMachineStatus(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                DyeMachine dyeMachine = machineService.getDyeMachinesById(id);
                if (dyeMachine == null) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy máy nhuộm!");
                    return "redirect:/technical/view-all-machine?tab=dye";
                }
                boolean newStatus = !dyeMachine.isActive();
                machineService.updateDyeMachineStatus(id, newStatus);
                redirectAttributes.addFlashAttribute("success", "Trạng thái máy nhuộm đã được cập nhật!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            }
            return "redirect:/technical/view-all-machine?tab=dye";
        }
        return "redirect:/login";
    }

    @GetMapping("/winding-machine/{id}/toggle-status")
    public String toggleWindingMachineStatus(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WindingMachine windingMachine = machineService.getWindingMachinesById(id);
                if (windingMachine == null) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy máy cuốn!");
                    return "redirect:/technical/view-all-machine?tab=winding";
                }
                boolean newStatus = !windingMachine.isActive();
                machineService.updateWindingMachineStatus(id, newStatus);
                redirectAttributes.addFlashAttribute("success", "Trạng thái máy cuốn đã được cập nhật!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            }
            return "redirect:/technical/view-all-machine?tab=winding";
        }
        return "redirect:/login";
    }
}