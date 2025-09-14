package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.error.ApiError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ProductDataExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError dataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg == null || msg.isBlank()) {
            msg = ex.getMessage();
        }
        return ApiError.conflict("Data integrity violation: " + msg, req.getRequestURI());
    }
}
