package com.trycore.evmTracker.domain.model;

import java.math.BigDecimal;

public record ActivityIndicators(
        BigDecimal plannedValue,
        BigDecimal earnedValue,
        BigDecimal costVariance,
        BigDecimal scheduleVariance,
        IndicatorResult costPerformanceIndex,
        IndicatorResult schedulePerformanceIndex,
        IndicatorResult estimateAtCompletion,
        IndicatorResult varianceAtCompletion,
        CostInterpretation costInterpretation,
        ScheduleInterpretation scheduleInterpretation
) {

    public enum CostInterpretation {
        EFICIENTE_EN_COSTOS,
        EN_PRESUPUESTO,
        SOBRE_PRESUPUESTO,
        NO_DETERMINADO
    }

    public enum ScheduleInterpretation {
        ADELANTADO,
        SEGUN_CRONOGRAMA,
        ATRASADO,
        NO_DETERMINADO
    }
}
