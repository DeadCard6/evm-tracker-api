package com.trycore.evmTracker.application.service;

import com.trycore.evmTracker.domain.model.Activity;
import com.trycore.evmTracker.domain.model.ActivityIndicators;
import com.trycore.evmTracker.domain.model.Project;
import com.trycore.evmTracker.domain.repository.ActivityRepository;
import com.trycore.evmTracker.domain.repository.ProjectRepository;
import com.trycore.evmTracker.application.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ActivityRepository activityRepository;
    private final EvmCalculationService evmCalculationService;

    public ProjectService(ProjectRepository projectRepository,
                          ActivityRepository activityRepository,
                          EvmCalculationService evmCalculationService) {
        this.projectRepository = projectRepository;
        this.activityRepository = activityRepository;
        this.evmCalculationService = evmCalculationService;
    }

    public Project create(String name) {
        return projectRepository.save(new Project(name));
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proyecto no encontrado: " + id));
    }

    public Project update(Long id, String name) {
        findById(id);
        Project project = new Project(name);
        project.setId(id);
        return projectRepository.save(project);
    }

    public void delete(Long id) {
        findById(id);
        activityRepository.deleteByProjectId(id);
        projectRepository.deleteById(id);
    }

    public ActivityIndicators calculateIndicators(Long id) {
        findById(id);
        List<Activity> activities = activityRepository.findByProjectId(id);
        return evmCalculationService.calculateConsolidatedIndicators(activities);
    }
}
