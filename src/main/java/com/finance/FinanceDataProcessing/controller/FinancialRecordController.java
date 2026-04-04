package com.finance.FinanceDataProcessing.controller;


import com.finance.FinanceDataProcessing.dtos.FinancialRecordRequest;
import com.finance.FinanceDataProcessing.dtos.FinancialRecordResponse;
import com.finance.FinanceDataProcessing.model.TransactionType;
import com.finance.FinanceDataProcessing.model.User;
import com.finance.FinanceDataProcessing.security.JwtUtil;
import com.finance.FinanceDataProcessing.service.AuthService;
import com.finance.FinanceDataProcessing.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@Tag(name = "Financial Records", description = "Financial record management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class FinancialRecordController {

    @Autowired
    private FinancialRecordService recordService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Create a new financial record
     * POST /api/records
     */
    @PostMapping
    @Operation(summary = "Create financial record", description = "Create a new financial record (Analyst/Admin only)")
    public ResponseEntity<FinancialRecordResponse> createRecord(
            @Valid @RequestBody FinancialRecordRequest request,
            HttpServletRequest servletRequest) {
        log.info("Creating new financial record");
        User currentUser = getCurrentUser(servletRequest);
        FinancialRecordResponse response = recordService.createRecord(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all records with pagination
     * GET /api/records
     */
    @GetMapping
    @Operation(summary = "Get all records", description = "Retrieve all financial records with pagination")
    public ResponseEntity<Page<FinancialRecordResponse>> getRecords(
            Pageable pageable,
            HttpServletRequest request) {
        log.info("Fetching all records");
        User currentUser = getCurrentUser(request);
        Page<FinancialRecordResponse> records = recordService.getRecords(currentUser, pageable);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    /**
     * Get records filtered by type
     * GET /api/records/filter/type?type=INCOME
     */
    @GetMapping("/filter/type")
    @Operation(summary = "Get records by type", description = "Retrieve records filtered by transaction type (INCOME/EXPENSE)")
    public ResponseEntity<Page<FinancialRecordResponse>> getRecordsByType(
            @RequestParam TransactionType type,
            Pageable pageable,
            HttpServletRequest request) {
        log.info("Fetching records by type: {}", type);
        User currentUser = getCurrentUser(request);
        Page<FinancialRecordResponse> records = recordService.getRecordsByType(currentUser, type, pageable);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    /**
     * Get records filtered by category
     * GET /api/records/filter/category?category=Groceries
     */
    @GetMapping("/filter/category")
    @Operation(summary = "Get records by category", description = "Retrieve records filtered by category")
    public ResponseEntity<Page<FinancialRecordResponse>> getRecordsByCategory(
            @RequestParam String category,
            Pageable pageable,
            HttpServletRequest request) {
        log.info("Fetching records by category: {}", category);
        User currentUser = getCurrentUser(request);
        Page<FinancialRecordResponse> records = recordService.getRecordsByCategory(currentUser, category, pageable);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    /**
     * Get records filtered by date range
     * GET /api/records/filter/date-range?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/filter/date-range")
    @Operation(summary = "Get records by date range", description = "Retrieve records filtered by date range")
    public ResponseEntity<Page<FinancialRecordResponse>> getRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable,
            HttpServletRequest request) {
        log.info("Fetching records for date range: {} to {}", startDate, endDate);
        User currentUser = getCurrentUser(request);
        Page<FinancialRecordResponse> records = recordService.getRecordsByDateRange(currentUser, startDate, endDate, pageable);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    /**
     * Get a specific record by ID
     * GET /api/records/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get record by ID", description = "Retrieve a specific financial record by ID")
    public ResponseEntity<FinancialRecordResponse> getRecordById(
            @PathVariable Long id,
            HttpServletRequest request) {
        log.info("Fetching record: {}", id);
        User currentUser = getCurrentUser(request);
        FinancialRecordResponse response = recordService.getRecordById(id, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update a financial record
     * PUT /api/records/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update record", description = "Update an existing financial record")
    public ResponseEntity<FinancialRecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordRequest request,
            HttpServletRequest servletRequest) {
        log.info("Updating record: {}", id);
        User currentUser = getCurrentUser(servletRequest);
        FinancialRecordResponse response = recordService.updateRecord(id, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a financial record (ADMIN only)
     * DELETE /api/records/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete record", description = "Delete a financial record (Admin only)")
    public ResponseEntity<Map<String, String>> deleteRecord(
            @PathVariable Long id,
            HttpServletRequest request) {
        log.info("Deleting record: {}", id);
        User currentUser = getCurrentUser(request);
        recordService.deleteRecord(id, currentUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Record deleted successfully");
        response.put("recordId", id.toString());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Helper method to extract current user from JWT token
     */
    private User getCurrentUser(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        String email = jwtUtil.extractEmail(token);
        return authService.getUserByEmail(email);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Invalid or missing Authorization header");
    }
}