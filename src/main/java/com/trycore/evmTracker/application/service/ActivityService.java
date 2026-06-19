package com.trycore.evmTracker.application.service;

import com.trycore.evmTracker.domain.model.Activity;
import com.trycore.evmTracker.domain.repository.ActivityRepository;
import com.trycore.evmTracker.application.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ProjectService projectService;

    public ActivityService(ActivityRepository activityRepository, ProjectService projectService) {
        this.activityRepository = activityRepository;
        this.projectService = projectService;
    }

    public Activity create(Long projectId,
                           Long userId,
                           String name,
                           BigDecimal bac,
                           BigDecimal plannedPercentComplete,
                           BigDecimal actualPercentComplete,
                           BigDecimal actualCost) {
        projectService.findByIdAndUser(projectId, userId);
        Activity activity = new Activity(
                projectId,
                name,
                bac,
                plannedPercentComplete,
                actualPercentComplete,
                actualCost
        );
        return activityRepository.save(activity);
    }

    public List<Activity> findByProjectId(Long projectId, Long userId) {
        projectService.findByIdAndUser(projectId, userId);
        return activityRepository.findByProjectId(projectId);
    }

    public Activity findById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Actividad no encontrada: " + id));
    }

    public Activity update(Long id,
                           Long userId,
                           String name,
                           BigDecimal bac,
                           BigDecimal plannedPercentComplete,
                           BigDecimal actualPercentComplete,
                           BigDecimal actualCost) {
        Activity current = findById(id);
        projectService.findByIdAndUser(current.getProjectId(), userId);
        Activity activity = new Activity(
                current.getProjectId(),
                name,
                bac,
                plannedPercentComplete,
                actualPercentComplete,
                actualCost
        );
        activity.setId(id);
        return activityRepository.save(activity);
    }

    public void delete(Long id, Long userId) {
        Activity current = findById(id);
        projectService.findByIdAndUser(current.getProjectId(), userId);
        activityRepository.deleteById(id);
    }
}
