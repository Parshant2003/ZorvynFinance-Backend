package com.finance.FinanceDataProcessing.model;

public enum Role {
    VIEWER("VIEWER", "Can only view dashboard data"),
    ANALYST("ANALYST", "Can view records and access insights"),
    ADMIN("ADMIN", "Can create, update, and manage records and users");

    private final String roleName;
    private final String description;

    Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }

    // Sabhi roles records dekh sakte hain (VIEWER bhi)
    public boolean canViewRecords() {
        return this == VIEWER || this == ANALYST || this == ADMIN;
    }

    // Sirf ADMIN ke liye special “any record” permission
    public boolean canViewAnyRecord() {
        return this == ADMIN;
    }

    // ANALYST + ADMIN → record create
    public boolean canCreateRecords() {
        return this == ANALYST || this == ADMIN;
    }

    // ANALYST + ADMIN → record update
    public boolean canUpdateRecords() {
        return this == ANALYST || this == ADMIN;
    }

    // Sirf ADMIN → delete
    public boolean canDeleteRecords() {
        return this == ADMIN;
    }

    // Sirf ADMIN → user manage
    public boolean canManageUsers() {
        return this == ADMIN;
    }

    public boolean canUpdateAnyRecord() {
        return this == ANALYST || this == ADMIN;
    }
}