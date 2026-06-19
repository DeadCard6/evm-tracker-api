package com.trycore.evmTracker.domain.repository;

import com.trycore.evmTracker.domain.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(Long id);
    List<Project> findAll();
    void deleteById(Long id);
}