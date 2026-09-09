package BookingHomeStay.BookingHomeStay.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "loi/404";
    }

    @ExceptionHandler(OwnershipException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleOwnership(OwnershipException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "loi/403";
    }

    @ExceptionHandler(BookingConflictException.class)
    public String handleConflict(BookingConflictException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "loi/404";
    }
}