package com.trycore.evmTracker.presentation.rest;

import com.trycore.evmTracker.application.service.ProjectService;
import com.trycore.evmTracker.domain.model.ActivityIndicators;
import com.trycore.evmTracker.domain.model.Project;
import com.trycore.evmTracker.presentation.rest.dto.ProjectRequest;
import com.trycore.evmTracker.presentation.rest.dto.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request) {
        Project project = projectService.create(request.name());
        return ProjectResponse.from(project);
    }

    @GetMapping
    public List<ProjectResponse> findAll() {
        return projectService.findAll().stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable Long id) {
        return ProjectResponse.from(projectService.findById(id));
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return ProjectResponse.from(projectService.update(id, request.name()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }

    @GetMapping("/{id}/indicators")
    public ActivityIndicators calculateIndicators(@PathVariable Long id) {
        return projectService.calculateIndicators(id);
    }
}
