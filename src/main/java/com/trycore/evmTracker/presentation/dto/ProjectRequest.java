package com.trycore.evmTracker.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
        @NotBlank(message = "name no puede estar vacio")
        String name
) {
}
