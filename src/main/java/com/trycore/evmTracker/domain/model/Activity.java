package com.trycore.evmTracker.domain.model;

import java.math.BigDecimal;

public class Activity {

    private Long id;
    private final Long projectId;
    private final String name;
    private final BigDecimal bac;
    private final BigDecimal plannedPercentComplete;
    private BigDecimal actualPercentComplete;
    private BigDecimal actualCost;

    public Activity(Long projectId, String name, BigDecimal bac,
                     BigDecimal plannedPercentComplete,
                     BigDecimal actualPercentComplete,
                     BigDecimal actualCost) {
        validatePercentage(plannedPercentComplete, "plannedPercentComplete");
        validatePercentage(actualPercentComplete, "actualPercentComplete");
        validateNonNegative(bac, "bac");
        validateNonNegative(actualCost, "actualCost");

        this.projectId = projectId;
        this.name = name;
        this.bac = bac;
        this.plannedPercentComplete = plannedPercentComplete;
        this.actualPercentComplete = actualPercentComplete;
        this.actualCost = actualCost;
    }

    private static void validatePercentage(BigDecimal value, String field) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0
                || value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(field + " debe estar entre 0 y 100");
        }
    }

    private static void validateNonNegative(BigDecimal value, String field) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(field + " no puede ser negativo");
        }
    }

    public void updateProgress(BigDecimal actualPercentComplete, BigDecimal actualCost) {
        validatePercentage(actualPercentComplete, "actualPercentComplete");
        validateNonNegative(actualCost, "actualCost");
        this.actualPercentComplete = actualPercentComplete;
        this.actualCost = actualCost;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjectId() { return projectId; }
    public String getName() { return name; }
    public BigDecimal getBac() { return bac; }
    public BigDecimal getPlannedPercentComplete() { return plannedPercentComplete; }
    public BigDecimal getActualPercentComplete() { return actualPercentComplete; }
    public BigDecimal getActualCost() { return actualCost; }
}