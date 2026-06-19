package com.trycore.evmTracker.presentation.controller;

import com.trycore.evmTracker.application.service.ActivityService;
import com.trycore.evmTracker.application.service.EvmCalculationService;
import com.trycore.evmTracker.domain.model.Activity;
import com.trycore.evmTracker.presentation.dto.ActivityRequest;
import com.trycore.evmTracker.presentation.dto.ActivityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.trycore.evmTracker.presentation.exception.ApiError;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Activities", description = "Activity management and EVM calculations")
@SecurityRequirement(name = "bearer-jwt")
@RestController
public class ActivityController {

    private final ActivityService activityService;
    private final EvmCalculationService evmCalculationService;

    public ActivityController(ActivityService activityService, EvmCalculationService evmCalculationService) {
        this.activityService = activityService;
        this.evmCalculationService = evmCalculationService;
    }

    @Operation(summary = "Create activity", description = "Create a new activity for the specified project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Activity created successfully", content = @Content(schema = @Schema(implementation = ActivityResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid activity data", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

    @Operation(summary = "List project activities", description = "Retrieve all activities for a given project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities for project", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/projects/{projectId}/activities")
    public List<ActivityResponse> findByProjectId(@PathVariable Long projectId) {
        return activityService.findByProjectId(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Operation(summary = "Get activity by id", description = "Retrieve a single activity by its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity found", content = @Content(schema = @Schema(implementation = ActivityResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/activities/{id}")
    public ActivityResponse findById(@PathVariable Long id) {
        return toResponse(activityService.findById(id));
    }

    @Operation(summary = "Update activity", description = "Update the details of an existing activity.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity updated", content = @Content(schema = @Schema(implementation = ActivityResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid activity data", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

    @Operation(summary = "Delete activity", description = "Delete an existing activity by its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activity deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/activities/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        activityService.delete(id);
    }

    private ActivityResponse toResponse(Activity activity) {
        return ActivityResponse.from(activity, evmCalculationService.calculateActivityIndicators(activity));
    }
}
