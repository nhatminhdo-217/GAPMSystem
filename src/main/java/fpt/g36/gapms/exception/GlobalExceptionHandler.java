package fpt.g36.gapms.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Xử lý lỗi 404
    @ExceptionHandler(RuntimeException.class)
    public String runTimeException(Model model) {
        // Thêm thông điệp lỗi vào model (nếu cần)
        model.addAttribute("message", "Page not found");
        return "error-404"; // Trả về trang lỗi 404
    }

    // Xử lý lỗi 403
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(Model model) {
        // Thêm thông điệp lỗi vào model nếu cần
        model.addAttribute("message", "Bạn không có quyền truy cập vào trang này");
        return "error-403";  // Trang lỗi 403 tùy chỉnh
    }

    // Xử lý lỗi 500
    @ExceptionHandler(Exception.class)
    public String handleException(Model model) {
        // Thêm thông điệp lỗi vào model (nếu cần)
        model.addAttribute("message", "Internal Server Error");
        return "error-500"; // Trả về trang lỗi 500
    }
}