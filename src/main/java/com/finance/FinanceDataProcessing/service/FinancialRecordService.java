package com.finance.FinanceDataProcessing.service;

import com.finance.FinanceDataProcessing.dtos.FinancialRecordRequest;
import com.finance.FinanceDataProcessing.dtos.FinancialRecordResponse;
import com.finance.FinanceDataProcessing.exception.AccessDeniedException;
import com.finance.FinanceDataProcessing.exception.ResourceNotFoundException;
import com.finance.FinanceDataProcessing.model.FinancialRecord;
import com.finance.FinanceDataProcessing.model.TransactionType;
import com.finance.FinanceDataProcessing.model.User;
import com.finance.FinanceDataProcessing.repository.FinancialRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.finance.FinanceDataProcessing.model.Role.ADMIN;

@Service
@Transactional
@Slf4j
public class FinancialRecordService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    /**
     * Create a financial record (ANALYST, ADMIN only)
     */
    public FinancialRecordResponse createRecord(FinancialRecordRequest request, User currentUser) {
        if (!currentUser.getRole().canCreateRecords()) {
            throw new AccessDeniedException("Your role does not have permission to create records");
        }

        log.info("Creating financial record for user: {}", currentUser.getId());

        FinancialRecord record = new FinancialRecord();
        record.setUser(currentUser);
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setDescription(request.getDescription());
        record.setDeleted(false);

        FinancialRecord savedRecord = recordRepository.save(record);
        log.info("Financial record created with ID: {}", savedRecord.getId());

        return mapToResponse(savedRecord);
    }

    /**
     * Get all records (sab roles sabhi records dekh sakte hain)
     */
    public Page<FinancialRecordResponse> getRecords(User currentUser, Pageable pageable) {
        if (!currentUser.getRole().canViewRecords()) {
            throw new AccessDeniedException("Your role does not have permission to view records");
        }

        // Sab roles ke liye sab records (VIEWER / ANALYST / ADMIN)
        Page<FinancialRecord> results = recordRepository.findByDeletedFalseOrderByDateDesc(pageable);

        log.info("Fetching records for user: {}, role: {}", currentUser.getId(), currentUser.getRole());
        return results.map(this::mapToResponse);
    }

    /**
     * Get records by type (sab roles sabhi records dekh sakte hain)
     */
    public Page<FinancialRecordResponse> getRecordsByType(User currentUser, TransactionType type, Pageable pageable) {
        if (!currentUser.getRole().canViewRecords()) {
            throw new AccessDeniedException("Your role does not have permission to view records");
        }

        Page<FinancialRecord> results = recordRepository.findByTypeAndDeletedFalseOrderByDateDesc(type, pageable);

        log.info("Fetching records by type: {} for user: {}", type, currentUser.getId());
        return results.map(this::mapToResponse);
    }

    /**
     * Get records by category (sab roles sabhi records dekh sakte hain)
     */
    public Page<FinancialRecordResponse> getRecordsByCategory(User currentUser, String category, Pageable pageable) {
        if (!currentUser.getRole().canViewRecords()) {
            throw new AccessDeniedException("Your role does not have permission to view records");
        }

        Page<FinancialRecord> results = recordRepository.findByCategoryAndDeletedFalseOrderByDateDesc(category, pageable);

        log.info("Fetching records by category: {} for user: {}", category, currentUser.getId());
        return results.map(this::mapToResponse);
    }

    /**
     * Get records by date range (sab roles sabhi records dekh sakte hain)
     */
    public Page<FinancialRecordResponse> getRecordsByDateRange(User currentUser, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (!currentUser.getRole().canViewRecords()) {
            throw new AccessDeniedException("Your role does not have permission to view records");
        }

        Page<FinancialRecord> results = recordRepository.findByDateBetweenAndDeletedFalseOrderByDateDesc(
                startDate, endDate, pageable);

        log.info("Fetching records for date range: {} to {} for user: {}", startDate, endDate, currentUser.getId());
        return results.map(this::mapToResponse);
    }

    /**
     * Get a specific record by ID (sab roles sabhi IDs dekh sakte hain)
     */
    public FinancialRecordResponse getRecordById(Long id, User currentUser) {
        if (!currentUser.getRole().canViewRecords()) {
            throw new AccessDeniedException("Your role does not have permission to view records");
        }

        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));

        log.info("Fetching record: {}", id);
        return mapToResponse(record);
    }

    /**
     * Update a financial record (ANALYST, ADMIN)
     * - ANALYST: sirf apna record update kar sakta hai
     * - ADMIN: kisi ka bhi record update kar sakta hai
     */
    public FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest request, User currentUser) {
        if (!currentUser.getRole().canUpdateRecords()) {
            throw new AccessDeniedException("Your role does not have permission to update records");
        }

        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));

        // ADMIN → kisi ka bhi record update kar sakta hai
        // ANALYST → sirf apna record update kar sakta hai
        if (!currentUser.getRole().canUpdateAnyRecord() &&
                !record.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own records");
        }

        log.info("Updating record: {}", id);

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setDescription(request.getDescription());

        FinancialRecord updatedRecord = recordRepository.save(record);
        return mapToResponse(updatedRecord);
    }

    /**
     * Delete a financial record (ADMIN only)
     */
    public void deleteRecord(Long id, User currentUser) {
        if (!currentUser.getRole().canDeleteRecords()) {
            throw new AccessDeniedException("Your role does not have permission to delete records");
        }

        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));

        record.softDelete();
        recordRepository.save(record);

        log.info("Record deleted (soft): {}", id);
    }

    /**
     * Map FinancialRecord entity to FinancialRecordResponse DTO
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