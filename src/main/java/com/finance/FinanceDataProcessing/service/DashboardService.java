package com.finance.FinanceDataProcessing.service;

import com.finance.FinanceDataProcessing.dtos.CategoryBreakdown;
import com.finance.FinanceDataProcessing.dtos.DashboardSummary;
import com.finance.FinanceDataProcessing.dtos.FinancialRecordResponse;
import com.finance.FinanceDataProcessing.exception.AccessDeniedException;
import com.finance.FinanceDataProcessing.model.FinancialRecord;
import com.finance.FinanceDataProcessing.model.TransactionType;
import com.finance.FinanceDataProcessing.model.User;
import com.finance.FinanceDataProcessing.repository.FinancialRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class DashboardService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    /**
     * Get dashboard summary (total income, expenses, net balance)
     */
    public DashboardSummary getSummary(User currentUser) {
        // Check access control
        if (!currentUser.getRole().canViewRecords()) {
            throw new AccessDeniedException("Your role does not have permission to view dashboard");
        }

        log.info("Generating dashboard summary for user: {}", currentUser.getId());

        BigDecimal totalIncome = recordRepository.sumByUserAndType(currentUser, TransactionType.INCOME);
        totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;

        BigDecimal totalExpenses = recordRepository.sumByUserAndType(currentUser, TransactionType.EXPENSE);
        totalExpenses = totalExpenses != null ? totalExpenses : BigDecimal.ZERO;

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        List<FinancialRecord> allRecords = recordRepository.findByUserAndDeletedFalseOrderByDateDesc(currentUser);
        long recordCount = allRecords.size();

        return new DashboardSummary(
                totalIncome,
                totalExpenses,
                netBalance,
                recordCount,
                "INR"
        );
    }

    /**
     * Get category wise breakdown
     */
    public List<CategoryBreakdown> getCategoryWiseBreakdown(User currentUser) {
        // Check access control
        if (!currentUser.getRole().canViewRecords()) {
            throw new AccessDeniedException("Your role does not have permission to view dashboard");
        }

        log.info("Generating category wise breakdown for user: {}", currentUser.getId());

        List<FinancialRecord> allRecords = recordRepository.findByUserAndDeletedFalseOrderByDateDesc(currentUser);

        // Group by category and type
        Map<String, Map<TransactionType, List<FinancialRecord>>> groupedByCategory = allRecords.stream()
                .collect(Collectors.groupingBy(
                        FinancialRecord::getCategory,
                        Collectors.groupingBy(FinancialRecord::getType)
                ));

        List<CategoryBreakdown> breakdown = new ArrayList<>();

        for (Map.Entry<String, Map<TransactionType, List<FinancialRecord>>> categoryEntry : groupedByCategory.entrySet()) {
            String category = categoryEntry.getKey();
            Map<TransactionType, List<FinancialRecord>> typeMap = categoryEntry.getValue();

            for (Map.Entry<TransactionType, List<FinancialRecord>> typeEntry : typeMap.entrySet()) {
                TransactionType type = typeEntry.getKey();
                List<FinancialRecord> records = typeEntry.getValue();

                BigDecimal total = records.stream()
                        .map(FinancialRecord::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                breakdown.add(new CategoryBreakdown(
                        category,
                        type.name(),
                        total,
                        records.size()
                ));
            }
        }

        return breakdown.stream()
                .sorted((a, b) -> b.getTotal().compareTo(a.getTotal()))
                .collect(Collectors.toList());
    }

    /**
     * Get recent transactions
     */
    public List<FinancialRecordResponse> getRecentTransactions(User currentUser, int limit) {
        // Check access control
        if (!currentUser.getRole().canViewRecords()) {
            throw new AccessDeniedException("Your role does not have permission to view dashboard");
        }

        log.info("Fetching recent {} transactions for user: {}", limit, currentUser.getId());

        Pageable pageable = PageRequest.of(0, limit);
        return recordRepository.findByUserAndDeletedFalseOrderByDateDescIdDesc(currentUser, pageable)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get monthly trends
     */
    public Map<String, Map<String, BigDecimal>> getMonthlyTrends(User currentUser) {
        // Check access control
        if (!currentUser.getRole().canViewRecords()) {
            throw new AccessDeniedException("Your role does not have permission to view dashboard");
        }

        log.info("Generating monthly trends for user: {}", currentUser.getId());

        List<FinancialRecord> allRecords = recordRepository.findByUserAndDeletedFalseOrderByDateDesc(currentUser);

        // Group by year-month
        Map<String, Map<String, BigDecimal>> monthlyTrends = new TreeMap<>(Collections.reverseOrder());

        for (FinancialRecord record : allRecords) {
            String yearMonth = String.format("%d-%02d",
                    record.getDate().getYear(),
                    record.getDate().getMonthValue());

            monthlyTrends.putIfAbsent(yearMonth, new HashMap<>());
            Map<String, BigDecimal> monthData = monthlyTrends.get(yearMonth);

            String typeKey = record.getType().name().toLowerCase();
            monthData.put(typeKey, monthData.getOrDefault(typeKey, BigDecimal.ZERO).add(record.getAmount()));
        }

        return monthlyTrends;
    }

    /**
     * Map FinancialRecord to FinancialRecordResponse
     */
    private FinancialRecordResponse mapToResponse(FinancialRecord record) {
        return new FinancialRecordResponse(
                record.getId(),
                record.getAmount(),
                record.getType(),
                record.getCategory(),
                record.getDate(),
                record.getDescription(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}