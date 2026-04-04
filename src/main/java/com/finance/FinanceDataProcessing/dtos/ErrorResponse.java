package com.finance.FinanceDataProcessing.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String status;
    private String message;
    private Integer code;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String path;
    private Map<String, String> errors;

    public ErrorResponse(String message, Integer code) {
        this.message = message;
        this.code = code;
        this.status = "ERROR";
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, Integer code, String path) {
        this.message = message;
        this.code = code;
        this.path = path;
        this.status = "ERROR";
        this.timestamp = LocalDateTime.now();
    }
}