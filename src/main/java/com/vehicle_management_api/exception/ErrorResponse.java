package com.vehicle_management_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String error;
    private String message;
    private Map<String, String> details;

    // Construtor para erros sem details
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}