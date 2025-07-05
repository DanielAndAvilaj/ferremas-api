package cl.ferremas.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejar errores de Thymeleaf y otros errores de vista
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        System.err.println("❌ ERROR GLOBAL: " + ex.getMessage());
        ex.printStackTrace();
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("error", "Ha ocurrido un error inesperado: " + ex.getMessage());
        mav.addObject("status", "500");
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    // Manejar errores 404 específicamente
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("error", "Página no encontrada");
        mav.addObject("status", "404");
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    // Manejar errores de recursos no encontrados
    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        // Para recursos estáticos, no mostrar página de error
        if (request.getRequestURI().startsWith("/css/") || 
            request.getRequestURI().startsWith("/js/") || 
            request.getRequestURI().startsWith("/img/") ||
            request.getRequestURI().startsWith("/.well-known/")) {
            return null; // Dejar que Spring maneje el 404 para recursos estáticos
        }
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("error", "Recurso no encontrado");
        mav.addObject("status", "404");
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    // Manejar errores de IO (incluyendo problemas de chunked encoding)
    @ExceptionHandler(IOException.class)
    public ModelAndView handleIOException(IOException ex, HttpServletRequest request) {
        System.err.println("❌ ERROR IO: " + ex.getMessage());
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("error", "Error de conexión: " + ex.getMessage());
        mav.addObject("status", "500");
        mav.addObject("path", request.getRequestURI());
        return mav;
    }
} 