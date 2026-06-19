package com.trycore.evmTracker.presentation.rest.dto;

import com.trycore.evmTracker.domain.model.Activity;
import com.trycore.evmTracker.domain.model.ActivityIndicators;

import java.math.BigDecimal;

public record ActivityResponse(
        Long id,
        Long projectId,
        String name,
        BigDecimal bac,
        BigDecimal plannedPercentComplete,
        BigDecimal actualPercentComplete,
        BigDecimal actualCost,
        ActivityIndicators indicators
) {

    public static ActivityResponse from(Activity activity, ActivityIndicators indicators) {
        return new ActivityResponse(
                activity.getId(),
                activity.getProjectId(),
                activity.getName(),
                activity.getBac(),
                activity.getPlannedPercentComplete(),
                activity.getActualPercentComplete(),
                activity.getActualCost(),
                indicators
        );
    }
}
