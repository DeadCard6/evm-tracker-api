package com.trycore.evmTracker.presentation.rest.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ActivityRequest(
        @NotBlank(message = "name no puede estar vacio")
        String name,

        @NotNull(message = "bac es requerido")
        @DecimalMin(value = "0.0", inclusive = true, message = "bac no puede ser negativo")
        BigDecimal bac,

        @NotNull(message = "plannedPercentComplete es requerido")
        @DecimalMin(value = "0.0", inclusive = true, message = "plannedPercentComplete debe estar entre 0 y 100")
        @DecimalMax(value = "100.0", inclusive = true, message = "plannedPercentComplete debe estar entre 0 y 100")
        BigDecimal plannedPercentComplete,

        @NotNull(message = "actualPercentComplete es requerido")
        @DecimalMin(value = "0.0", inclusive = true, message = "actualPercentComplete debe estar entre 0 y 100")
        @DecimalMax(value = "100.0", inclusive = true, message = "actualPercentComplete debe estar entre 0 y 100")
        BigDecimal actualPercentComplete,

        @NotNull(message = "actualCost es requerido")
        @DecimalMin(value = "0.0", inclusive = true, message = "actualCost no puede ser negativo")
        BigDecimal actualCost
) {
}
