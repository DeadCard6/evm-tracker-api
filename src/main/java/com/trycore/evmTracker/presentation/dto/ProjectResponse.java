package com.trycore.evmTracker.presentation.dto;

import com.trycore.evmTracker.domain.model.Project;

public record ProjectResponse(Long id, String name) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(project.getId(), project.getName());
    }
}
