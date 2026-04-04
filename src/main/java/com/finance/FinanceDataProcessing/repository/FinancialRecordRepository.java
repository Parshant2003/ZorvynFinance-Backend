package com.finance.FinanceDataProcessing.repository;

import com.finance.FinanceDataProcessing.model.FinancialRecord;
import com.finance.FinanceDataProcessing.model.TransactionType;
import com.finance.FinanceDataProcessing.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // ADMIN: sab records (pagination)
    Page<FinancialRecord> findByDeletedFalseOrderByDateDesc(Pageable pageable);

    // ADMIN: saare records by type
    Page<FinancialRecord> findByTypeAndDeletedFalseOrderByDateDesc(TransactionType type, Pageable pageable);

    // ADMIN: saare records by category
    Page<FinancialRecord> findByCategoryAndDeletedFalseOrderByDateDesc(String category, Pageable pageable);

    // ADMIN: saare records by date range
    Page<FinancialRecord> findByDateBetweenAndDeletedFalseOrderByDateDesc(
            LocalDate startDate, LocalDate endDate, Pageable pageable);

    // ADMIN: list of all records
    List<FinancialRecord> findByDeletedFalseOrderByDateDescIdDesc();

    // ADMIN: all records by type (no user filter)
    List<FinancialRecord> findByTypeAndDeletedFalseOrderByDateDesc(TransactionType type);

    // ADMIN: all records by category (no user filter)
    List<FinancialRecord> findByCategoryAndDeletedFalseOrderByDateDesc(String category);

    // ADMIN: all records by date range (no user filter)
    List<FinancialRecord> findByDateBetweenAndDeletedFalseOrderByDateDesc(
            LocalDate startDate, LocalDate endDate);


    // NORMAL USERS: sirf apna user (deleted = false)
    // Find records by user and not deleted (pagination)
    Page<FinancialRecord> findByUserAndDeletedFalseOrderByDateDesc(User user, Pageable pageable);

    // Find records by user and type, and not deleted (pagination)
    Page<FinancialRecord> findByUserAndTypeAndDeletedFalseOrderByDateDesc(User user, TransactionType type, Pageable pageable);

    // Find records by user and category, and not deleted (pagination)
    Page<FinancialRecord> findByUserAndCategoryAndDeletedFalseOrderByDateDesc(User user, String category, Pageable pageable);

    // Find records by user and date range, and not deleted (pagination)
    Page<FinancialRecord> findByUserAndDateBetweenAndDeletedFalseOrderByDateDesc(
            User user, LocalDate startDate, LocalDate endDate, Pageable pageable);


    // NORMAL: list APIs (sirf apna user)
    // Get all records for a user (not deleted)
    List<FinancialRecord> findByUserAndDeletedFalseOrderByDateDesc(User user);

    // Get all records by type for a user (not deleted)
    List<FinancialRecord> findByUserAndTypeAndDeletedFalse(User user, TransactionType type);


    // ADMIN & USER: aggregation

    // ADMIN: sum of all records by type (no user filter)
    @Query("SELECT SUM(fr.amount) FROM FinancialRecord fr WHERE fr.type = :type AND fr.deleted = false")
    BigDecimal sumAllByType(@Param("type") TransactionType type);

    // ADMIN: sum of all records by category (no user filter)
    @Query("SELECT SUM(fr.amount) FROM FinancialRecord fr WHERE fr.category = :category AND fr.deleted = false")
    BigDecimal sumAllByCategory(@Param("category") String category);

    // USER: sum by user and type
    @Query("SELECT SUM(fr.amount) FROM FinancialRecord fr WHERE fr.user = :user AND fr.type = :type AND fr.deleted = false")
    BigDecimal sumByUserAndType(@Param("user") User user, @Param("type") TransactionType type);

    // USER: sum by user and category
    @Query("SELECT SUM(fr.amount) FROM FinancialRecord fr WHERE fr.user = :user AND fr.category = :category AND fr.deleted = false")
    BigDecimal sumByUserAndCategory(@Param("user") User user, @Param("category") String category);

    // USER: distinct categories (apna user ke liye)
    @Query("SELECT DISTINCT fr.category FROM FinancialRecord fr WHERE fr.user = :user AND fr.deleted = false")
    List<String> findDistinctCategoriesByUser(@Param("user") User user);

    // USER: recent records (apna user ke liye, pagination)
    Page<FinancialRecord> findByUserAndDeletedFalseOrderByDateDescIdDesc(User user, Pageable pageable);
}