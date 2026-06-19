package com.trycore.evmTracker.application.service;

import com.trycore.evmTracker.domain.model.Activity;
import com.trycore.evmTracker.domain.model.ActivityIndicators;
import com.trycore.evmTracker.domain.model.ActivityIndicators.CostInterpretation;
import com.trycore.evmTracker.domain.model.ActivityIndicators.ScheduleInterpretation;
import com.trycore.evmTracker.domain.model.IndicatorResult;
import com.trycore.evmTracker.domain.model.IndicatorResult.IndicatorStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class EvmCalculationService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final int CALCULATION_SCALE = 4;

    public ActivityIndicators calculateActivityIndicators(Activity activity) {
        BigDecimal pv = percentageOf(activity.getBac(), activity.getPlannedPercentComplete());
        BigDecimal ev = percentageOf(activity.getBac(), activity.getActualPercentComplete());
        return buildIndicators(activity.getBac(), pv, ev, activity.getActualCost());
    }

    public ActivityIndicators calculateConsolidatedIndicators(List<Activity> activities) {
        BigDecimal totalBac = BigDecimal.ZERO;
        BigDecimal totalPv = BigDecimal.ZERO;
        BigDecimal totalEv = BigDecimal.ZERO;
        BigDecimal totalAc = BigDecimal.ZERO;

        for (Activity activity : activities) {
            totalBac = totalBac.add(activity.getBac());
            totalPv = totalPv.add(percentageOf(activity.getBac(), activity.getPlannedPercentComplete()));
            totalEv = totalEv.add(percentageOf(activity.getBac(), activity.getActualPercentComplete()));
            totalAc = totalAc.add(activity.getActualCost());
        }

        return buildIndicators(totalBac, totalPv, totalEv, totalAc);
    }

    private ActivityIndicators buildIndicators(BigDecimal bac, BigDecimal pv, BigDecimal ev, BigDecimal ac) {
        BigDecimal cv = ev.subtract(ac);
        BigDecimal sv = ev.subtract(pv);
        IndicatorResult cpi = divide(ev, ac);
        IndicatorResult spi = divide(ev, pv);
        IndicatorResult eac = estimateAtCompletion(bac, cpi);
        IndicatorResult vac = varianceAtCompletion(bac, eac);
        return new ActivityIndicators(
                pv,
                ev,
                cv,
                sv,
                cpi,
                spi,
                eac,
                vac,
                interpretCost(cpi),
                interpretSchedule(spi)
        );
    }

    private CostInterpretation interpretCost(IndicatorResult cpi) {
        if (!cpi.isDefined()) {
            return CostInterpretation.NO_DETERMINADO;
        }
        int comparison = cpi.value().compareTo(BigDecimal.ONE);
        if (comparison > 0) {
            return CostInterpretation.EFICIENTE_EN_COSTOS;
        }
        if (comparison < 0) {
            return CostInterpretation.SOBRE_PRESUPUESTO;
        }
        return CostInterpretation.EN_PRESUPUESTO;
    }

    private ScheduleInterpretation interpretSchedule(IndicatorResult spi) {
        if (!spi.isDefined()) {
            return ScheduleInterpretation.NO_DETERMINADO;
        }
        int comparison = spi.value().compareTo(BigDecimal.ONE);
        if (comparison > 0) {
            return ScheduleInterpretation.ADELANTADO;
        }
        if (comparison < 0) {
            return ScheduleInterpretation.ATRASADO;
        }
        return ScheduleInterpretation.SEGUN_CRONOGRAMA;
    }

    private IndicatorResult estimateAtCompletion(BigDecimal bac, IndicatorResult cpi) {
        if (!cpi.isDefined()) {
            return IndicatorResult.undefined(IndicatorStatus.UNDEFINED_DEPENDENCY);
        }
        BigDecimal eac = bac.divide(cpi.value(), CALCULATION_SCALE, RoundingMode.HALF_UP);
        return IndicatorResult.calculated(eac);
    }

    private IndicatorResult varianceAtCompletion(BigDecimal bac, IndicatorResult eac) {
        if (!eac.isDefined()) {
            return IndicatorResult.undefined(IndicatorStatus.UNDEFINED_DEPENDENCY);
        }
        return IndicatorResult.calculated(bac.subtract(eac.value()));
    }

    private IndicatorResult divide(BigDecimal numerator, BigDecimal denominator) {
        boolean denominatorIsZero = denominator.compareTo(BigDecimal.ZERO) == 0;
        boolean numeratorIsZero = numerator.compareTo(BigDecimal.ZERO) == 0;

        if (denominatorIsZero && numeratorIsZero) {
            return IndicatorResult.undefined(IndicatorStatus.UNDEFINED_NO_BASIS);
        }
        if (denominatorIsZero) {
            return IndicatorResult.undefined(IndicatorStatus.UNDEFINED_PROGRESS_WITHOUT_BASIS);
        }
        return IndicatorResult.calculated(numerator.divide(denominator, CALCULATION_SCALE, RoundingMode.HALF_UP));
    }

    private BigDecimal percentageOf(BigDecimal base, BigDecimal percent) {
        return base.multiply(percent).divide(ONE_HUNDRED, CALCULATION_SCALE, RoundingMode.HALF_UP);
    }
}
