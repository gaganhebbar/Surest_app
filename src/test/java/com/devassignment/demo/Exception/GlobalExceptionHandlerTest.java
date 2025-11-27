package com.devassignment.demo.Exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleValidation() {
        // Create a binding result with errors
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "objectName");

        bindingResult.addError(new FieldError("objectName", "firstName", "First name required"));
        bindingResult.addError(new FieldError("objectName", "lastName", "Last name required"));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<?> response = handler.handleValidation(exception);

        assertEquals(400, response.getStatusCode().value());

        Map<String, String> body = (Map<String, String>) response.getBody();

        assertNotNull(body);
        assertEquals(2, body.size());
        assertEquals("First name required", body.get("firstName"));
        assertEquals("Last name required", body.get("lastName"));
    }

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad argument");

        ResponseEntity<?> response = handler.handleIllegalArgument(ex);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("Bad argument", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testHandleNotFound() {
        RuntimeException ex = new RuntimeException("Resource not found");

        ResponseEntity<?> response = handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Resource not found", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testHandleAuthErrors() {
        AuthenticationException ex = mock(AuthenticationException.class);
        when(ex.getMessage()).thenReturn("Invalid token");

        ResponseEntity<?> response = handler.handleAuthErrors(ex);

        assertEquals(401, response.getStatusCode().value());

        Map<String, String> body = (Map<String, String>) response.getBody();

        assertEquals("Unauthorized", body.get("error"));
        assertEquals("Invalid token", body.get("message"));
    }
}
