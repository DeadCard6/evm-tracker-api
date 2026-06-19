package com.trycore.evmTracker.presentation.controller;

import com.trycore.evmTracker.application.service.ProjectService;
import com.trycore.evmTracker.application.service.UserService;
import com.trycore.evmTracker.domain.model.ActivityIndicators;
import com.trycore.evmTracker.domain.model.Project;
import com.trycore.evmTracker.presentation.dto.ProjectRequest;
import com.trycore.evmTracker.presentation.dto.ProjectResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Projects", description = "Project CRUD operations and EVM indicator calculation")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName()).getId();
    }

    @Operation(summary = "Create project", description = "Create a new project with the provided name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid project data", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request) {
        var user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Project project = projectService.create(request.name(), user);
        return ProjectResponse.from(project);
    }

    @Operation(summary = "List projects", description = "Retrieve all projects available for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of projects", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public List<ProjectResponse> findAll() {
        Long userId = getAuthenticatedUserId();
        return projectService.findAllByUser(userId).stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Operation(summary = "Get project by id", description = "Retrieve a single project by its database identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();
        return ProjectResponse.from(projectService.findByIdAndUser(id, userId));
    }

    @Operation(summary = "Update project", description = "Update the name of an existing project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid project data", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        Long userId = getAuthenticatedUserId();
        return ProjectResponse.from(projectService.update(id, userId, request.name()));
    }

    @Operation(summary = "Delete project", description = "Delete an existing project by its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();
        projectService.delete(id, userId);
    }

    @Operation(summary = "Calculate indicators", description = "Calculate EVM indicators for a project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Indicators calculated", content = @Content(schema = @Schema(implementation = ActivityIndicators.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - bearer token missing or invalid", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}/indicators")
    public ActivityIndicators calculateIndicators(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();
        return projectService.calculateIndicators(id, userId);
    }
}

