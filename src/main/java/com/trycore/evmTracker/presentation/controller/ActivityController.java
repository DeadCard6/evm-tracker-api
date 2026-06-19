package com.trycore.evmTracker.presentation.controller;

import com.trycore.evmTracker.application.service.ActivityService;
import com.trycore.evmTracker.application.service.EvmCalculationService;
import com.trycore.evmTracker.domain.model.Activity;
import com.trycore.evmTracker.presentation.dto.ActivityRequest;
import com.trycore.evmTracker.presentation.dto.ActivityResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivityController {

    private final ActivityService activityService;
    private final EvmCalculationService evmCalculationService;

    public ActivityController(ActivityService activityService, EvmCalculationService evmCalculationService) {
        this.activityService = activityService;
        this.evmCalculationService = evmCalculationService;
    }

    @PostMapping("/projects/{projectId}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityResponse create(@PathVariable Long projectId, @Valid @RequestBody ActivityRequest request) {
        Activity activity = activityService.create(
                projectId,
                request.name(),
                request.bac(),
                request.plannedPercentComplete(),
                request.actualPercentComplete(),
                request.actualCost()
        );
        return toResponse(activity);
    }

    @GetMapping("/projects/{projectId}/activities")
    public List<ActivityResponse> findByProjectId(@PathVariable Long projectId) {
        return activityService.findByProjectId(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/activities/{id}")
    public ActivityResponse findById(@PathVariable Long id) {
        return toResponse(activityService.findById(id));
    }

    @PutMapping("/activities/{id}")
    public ActivityResponse update(@PathVariable Long id, @Valid @RequestBody ActivityRequest request) {
        Activity activity = activityService.update(
                id,
                request.name(),
                request.bac(),
                request.plannedPercentComplete(),
                request.actualPercentComplete(),
                request.actualCost()
        );
        return toResponse(activity);
    }

    @DeleteMapping("/activities/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        activityService.delete(id);
    }

    private ActivityResponse toResponse(Activity activity) {
        return ActivityResponse.from(activity, evmCalculationService.calculateActivityIndicators(activity));
    }
}
