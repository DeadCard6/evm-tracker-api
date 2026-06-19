package com.trycore.evmTracker.infrastructure.persistence;

import com.trycore.evmTracker.domain.model.Project;
import com.trycore.evmTracker.domain.repository.ProjectRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryProjectRepository implements ProjectRepository {

    private final Map<Long, Project> projects = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public Project save(Project project) {
        if (project.getId() == null) {
            project.setId(sequence.getAndIncrement());
        }
        projects.put(project.getId(), project);
        return project;
    }

    @Override
    public Optional<Project> findById(Long id) {
        return Optional.ofNullable(projects.get(id));
    }

    @Override
    public List<Project> findAll() {
        List<Project> result = new ArrayList<>(projects.values());
        result.sort(Comparator.comparing(Project::getId));
        return result;
    }

    @Override
    public void deleteById(Long id) {
        projects.remove(id);
    }
}
