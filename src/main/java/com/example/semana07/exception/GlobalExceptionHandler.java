package com.example.semana07.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        if (esApi(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(cuerpoError(ex.getMessage()));
        }
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Object handleUnauthorized(UnauthorizedException ex, WebRequest request) {
        log.warn("Acceso no autorizado: {}", ex.getMessage());
        if (esApi(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(cuerpoError(ex.getMessage()));
        }
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("message", ex.getMessage());
        mav.setStatus(HttpStatus.FORBIDDEN);
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.warn("Spring Security denegó el acceso: {}", ex.getMessage());
        if (esApi(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(cuerpoError("No tienes permiso para esta acción"));
        }
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("message", "No tienes permiso para acceder a esta página");
        mav.setStatus(HttpStatus.FORBIDDEN);
        return mav;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.put(fe.getField(), fe.getDefaultMessage());
        }
        log.info("Validación fallida: {}", errores);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Datos inválidos");
        body.put("detalles", errores);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntime(RuntimeException ex, WebRequest request) {
        log.error("Error de negocio: {}", ex.getMessage());
        if (esApi(request)) {
            return ResponseEntity.badRequest().body(cuerpoError(ex.getMessage()));
        }
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("message", ex.getMessage());
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneral(Exception ex, WebRequest request) {
        log.error("Error inesperado", ex);
        if (esApi(request)) {
            return ResponseEntity.internalServerError().body(cuerpoError("Ocurrió un error inesperado"));
        }
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("message", "Ocurrió un error inesperado");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }

    private boolean esApi(WebRequest request) {
        String uri = request.getDescription(false);
        return uri != null && uri.contains("/api/");
    }

    private Map<String, Object> cuerpoError(String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", mensaje);
        body.put("timestamp", LocalDateTime.now());
        return body;
    }
}