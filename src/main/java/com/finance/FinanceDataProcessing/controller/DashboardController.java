package com.finance.FinanceDataProcessing.controller;


import com.finance.FinanceDataProcessing.dtos.CategoryBreakdown;
import com.finance.FinanceDataProcessing.dtos.DashboardSummary;
import com.finance.FinanceDataProcessing.dtos.FinancialRecordResponse;
import com.finance.FinanceDataProcessing.model.User;
import com.finance.FinanceDataProcessing.security.JwtUtil;
import com.finance.FinanceDataProcessing.service.AuthService;
import com.finance.FinanceDataProcessing.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard analytics and summary endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Get dashboard summary
     * GET /api/dashboard/summary
     */
    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary", description = "Retrieve total income, expenses, and net balance")
    public ResponseEntity<DashboardSummary> getSummary(HttpServletRequest request) {
        log.info("Fetching dashboard summary");
        User currentUser = getCurrentUser(request);
        DashboardSummary summary = dashboardService.getSummary(currentUser);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

    /**
     * Get category-wise breakdown
     * GET /api/dashboard/category-wise
     */
    @GetMapping("/category-wise")
    @Operation(summary = "Get category breakdown", description = "Retrieve income and expense breakdown by category")
    public ResponseEntity<List<CategoryBreakdown>> getCategoryWiseBreakdown(HttpServletRequest request) {
        log.info("Fetching category-wise breakdown");
        User currentUser = getCurrentUser(request);
        List<CategoryBreakdown> breakdown = dashboardService.getCategoryWiseBreakdown(currentUser);
        return new ResponseEntity<>(breakdown, HttpStatus.OK);
    }

    /**
     * Get recent transactions
     * GET /api/dashboard/recent?limit=10
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent transactions", description = "Retrieve recent transactions")
    public ResponseEntity<List<FinancialRecordResponse>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {
        log.info("Fetching recent transactions");
        User currentUser = getCurrentUser(request);
        List<FinancialRecordResponse> recent = dashboardService.getRecentTransactions(currentUser, limit);
        return new ResponseEntity<>(recent, HttpStatus.OK);
    }

    /**
     * Get monthly trends
     * GET /api/dashboard/trends
     */
    @GetMapping("/trends")
    @Operation(summary = "Get monthly trends", description = "Retrieve monthly income and expense trends")
    public ResponseEntity<Map<String, Map<String, Object>>> getMonthlyTrends(HttpServletRequest request) {
        log.info("Fetching monthly trends");
        User currentUser = getCurrentUser(request);
        Map<String, Map<String, Object>> trends = new HashMap<>();

        try {
            var rawTrends = dashboardService.getMonthlyTrends(currentUser);
            rawTrends.forEach((month, data) -> {
                Map<String, Object> convertedData = new HashMap<>();
                data.forEach((key, value) -> convertedData.put(key, value));
                trends.put(month, convertedData);
            });
        } catch (Exception e) {
            log.error("Error fetching trends", e);
        }

        return new ResponseEntity<>(trends, HttpStatus.OK);
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