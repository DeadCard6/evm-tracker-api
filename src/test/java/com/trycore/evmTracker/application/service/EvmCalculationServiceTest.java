package com.trycore.evmTracker.application.service;

import com.trycore.evmTracker.domain.model.Activity;
import com.trycore.evmTracker.domain.model.ActivityIndicators;
import com.trycore.evmTracker.domain.model.ActivityIndicators.CostInterpretation;
import com.trycore.evmTracker.domain.model.ActivityIndicators.ScheduleInterpretation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EvmCalculationServiceTest {

    private final EvmCalculationService service = new EvmCalculationService();

    @Test
    void interpretsEfficientCostAndAheadScheduleWhenIndexesAreGreaterThanOne() {
        Activity activity = new Activity(
                1L,
                "Desarrollo API",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(40),
                BigDecimal.valueOf(60),
                BigDecimal.valueOf(500)
        );

        ActivityIndicators indicators = service.calculateActivityIndicators(activity);

        assertThat(indicators.costPerformanceIndex().value()).isEqualByComparingTo("1.2000");
        assertThat(indicators.schedulePerformanceIndex().value()).isEqualByComparingTo("1.5000");
        assertThat(indicators.costInterpretation()).isEqualTo(CostInterpretation.EFICIENTE_EN_COSTOS);
        assertThat(indicators.scheduleInterpretation()).isEqualTo(ScheduleInterpretation.ADELANTADO);
    }

    @Test
    void interpretsOverBudgetAndBehindScheduleWhenIndexesAreLessThanOne() {
        Activity activity = new Activity(
                1L,
                "Integracion",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(80),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(700)
        );

        ActivityIndicators indicators = service.calculateActivityIndicators(activity);

        assertThat(indicators.costPerformanceIndex().value()).isEqualByComparingTo("0.7143");
        assertThat(indicators.schedulePerformanceIndex().value()).isEqualByComparingTo("0.6250");
        assertThat(indicators.costInterpretation()).isEqualTo(CostInterpretation.SOBRE_PRESUPUESTO);
        assertThat(indicators.scheduleInterpretation()).isEqualTo(ScheduleInterpretation.ATRASADO);
    }

    @Test
    void interpretsOnBudgetAndOnScheduleWhenIndexesAreEqualToOne() {
        Activity activity = new Activity(
                1L,
                "QA",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(500)
        );

        ActivityIndicators indicators = service.calculateActivityIndicators(activity);

        assertThat(indicators.costPerformanceIndex().value()).isEqualByComparingTo("1.0000");
        assertThat(indicators.schedulePerformanceIndex().value()).isEqualByComparingTo("1.0000");
        assertThat(indicators.costInterpretation()).isEqualTo(CostInterpretation.EN_PRESUPUESTO);
        assertThat(indicators.scheduleInterpretation()).isEqualTo(ScheduleInterpretation.SEGUN_CRONOGRAMA);
    }

    @Test
    void returnsUndeterminedInterpretationsWhenIndexesAreUndefined() {
        Activity activity = new Activity(
                1L,
                "Inicio",
                BigDecimal.valueOf(1000),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        ActivityIndicators indicators = service.calculateActivityIndicators(activity);

        assertThat(indicators.costPerformanceIndex().isDefined()).isFalse();
        assertThat(indicators.schedulePerformanceIndex().isDefined()).isFalse();
        assertThat(indicators.costInterpretation()).isEqualTo(CostInterpretation.NO_DETERMINADO);
        assertThat(indicators.scheduleInterpretation()).isEqualTo(ScheduleInterpretation.NO_DETERMINADO);
    }
}
