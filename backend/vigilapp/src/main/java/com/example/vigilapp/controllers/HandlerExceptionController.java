package com.example.vigilapp.controllers;
import java.util.*;

import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.vigilapp.exception.ProfesorNotFoundException;

public class HandlerExceptionController {

    @ExceptionHandler(ProfesorNotFoundException.class)
    public Map<String, String> handleProfesorNotFoundException(ProfesorNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Profesor no encontrado");
        errorResponse.put("message", ex.getMessage());
        return errorResponse;
    }
}
