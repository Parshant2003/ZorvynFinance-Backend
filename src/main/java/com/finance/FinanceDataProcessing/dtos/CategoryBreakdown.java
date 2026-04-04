package com.finance.FinanceDataProcessing.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBreakdown {

    private String category;
    private String type; // INCOME or EXPENSE
    private BigDecimal total;
    private Integer count;
}