package com.example.vigilapp.controllers;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.vigilapp.exception.CheckinTurnoNotFoundException;
import com.example.vigilapp.exception.CheckpointNotFoundException;
import com.example.vigilapp.exception.IncidenteNotFoundException;
import com.example.vigilapp.exception.MetricasDocenteNotFoundException;
import com.example.vigilapp.exception.NotificacionNotFoundException;
import com.example.vigilapp.exception.ReasignacionNotFoundException;
import com.example.vigilapp.exception.RolNotFoundException;
import com.example.vigilapp.exception.SeveridadNotFoundException;
import com.example.vigilapp.exception.TipoIncidenteNotFoundException;
import com.example.vigilapp.exception.TurnoNotFoundException;
import com.example.vigilapp.exception.UsuarioNotFoundException;
import com.example.vigilapp.exception.ZonaNotFoundException;



@RestControllerAdvice
public class HandlerExceptionController {

    @ExceptionHandler({
        CheckinTurnoNotFoundException.class,
        CheckpointNotFoundException.class,
        IncidenteNotFoundException.class,
        MetricasDocenteNotFoundException.class,
        NotificacionNotFoundException.class,
        ReasignacionNotFoundException.class,
        RolNotFoundException.class,
        SeveridadNotFoundException.class,
        TipoIncidenteNotFoundException.class,
        TurnoNotFoundException.class,
        UsuarioNotFoundException.class,
        ZonaNotFoundException.class,
    })
    public ResponseEntity<Map<String, String>> handleNotFoundException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Recurso no encontrado");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Datos de entrada inválidos");
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
        errorResponse.put("errors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Ruta no encontrada");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Error interno del servidor");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
