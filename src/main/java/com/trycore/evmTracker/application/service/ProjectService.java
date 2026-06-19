package com.trycore.evmTracker.application.service;

import com.trycore.evmTracker.domain.model.Activity;
import com.trycore.evmTracker.domain.model.ActivityIndicators;
import com.trycore.evmTracker.domain.model.Project;
import com.trycore.evmTracker.domain.model.User;
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

    public Project create(String name, User user) {
        return projectRepository.save(new Project(name, user));
    }

    public List<Project> findAllByUser(Long userId) {
        return projectRepository.findByUserId(userId);
    }

    public Project findByIdAndUser(Long id, Long userId) {
        return projectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Proyecto no encontrado: " + id));
    }

    public Project update(Long id, Long userId, String name) {
        Project project = findByIdAndUser(id, userId);
        project.setName(name);
        return projectRepository.save(project);
    }

    public void delete(Long id, Long userId) {
        findByIdAndUser(id, userId);
        activityRepository.deleteByProjectId(id);
        projectRepository.deleteByIdAndUserId(id, userId);
    }

    public ActivityIndicators calculateIndicators(Long id, Long userId) {
        findByIdAndUser(id, userId);
        List<Activity> activities = activityRepository.findByProjectId(id);
        return evmCalculationService.calculateConsolidatedIndicators(activities);
    }
}
