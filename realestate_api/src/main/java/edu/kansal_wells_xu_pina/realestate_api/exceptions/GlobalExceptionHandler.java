package edu.kansal_wells_xu_pina.realestate_api.exceptions;

import edu.kansal_wells_xu_pina.realestate_api.dtos.ApiExceptionDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiExceptionDto> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        ApiExceptionDto error = new ApiExceptionDto(
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getRequestURI(),
            ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiExceptionDto> handleAlreadyExistsException(AlreadyExistsException ex, HttpServletRequest request) {
        log.error("Resource already exists: {}", ex.getMessage());
        ApiExceptionDto error = new ApiExceptionDto(
            ex.getMessage(),
            HttpStatus.CONFLICT.value(),
            request.getRequestURI(),
            ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidUserParameterException.class)
    public ResponseEntity<ApiExceptionDto> handleInvalidUserParameterException(InvalidUserParameterException ex, HttpServletRequest request) {
        log.error("Invalid user parameter: {}", ex.getMessage());
        ApiExceptionDto error = new ApiExceptionDto(
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPropertyParameterException.class)
    public ResponseEntity<ApiExceptionDto> handleInvalidPropertyParameterException(InvalidPropertyParameterException ex, HttpServletRequest request) {
        log.error("Invalid property parameter: {}", ex.getMessage());
        ApiExceptionDto error = new ApiExceptionDto(
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPropertyImageParameterException.class)
    public ResponseEntity<ApiExceptionDto> handleInvalidPropertyImageParameterException(InvalidPropertyImageParameterException ex, HttpServletRequest request) {
        log.error("Invalid property image parameter: {}", ex.getMessage());
        ApiExceptionDto error = new ApiExceptionDto(
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiExceptionDto> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        log.error("Authentication failed: {}", ex.getMessage());
        ApiExceptionDto error = new ApiExceptionDto(
            "Invalid email or password",
            HttpStatus.UNAUTHORIZED.value(),
            request.getRequestURI(),
            ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiExceptionDto> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        ApiExceptionDto error = new ApiExceptionDto(
            "You don't have permission to access this resource",
            HttpStatus.FORBIDDEN.value(),
            request.getRequestURI(),
            ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiExceptionDto> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.error("File upload size exceeded: {}", ex.getMessage());
        ApiExceptionDto error = new ApiExceptionDto(
            "File size exceeds maximum limit",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionDto> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ApiExceptionDto error = new ApiExceptionDto(
            "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI(),
            ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
