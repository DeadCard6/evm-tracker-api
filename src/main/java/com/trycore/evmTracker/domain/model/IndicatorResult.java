package com.trycore.evmTracker.domain.model;

import java.math.BigDecimal;

public record IndicatorResult(BigDecimal value, IndicatorStatus status) {

    public static IndicatorResult calculated(BigDecimal value) {
        return new IndicatorResult(value, IndicatorStatus.CALCULATED);
    }

    public static IndicatorResult undefined(IndicatorStatus status) {
        if (status == IndicatorStatus.CALCULATED) {
            throw new IllegalArgumentException("CALCULATED requiere un valor; usa calculated(value)");
        }
        return new IndicatorResult(null, status);
    }

    public boolean isDefined() {
        return status == IndicatorStatus.CALCULATED;
    }

    public enum IndicatorStatus {
        CALCULATED,
        UNDEFINED_NO_BASIS,
        UNDEFINED_PROGRESS_WITHOUT_BASIS,
        UNDEFINED_DEPENDENCY
    }
}