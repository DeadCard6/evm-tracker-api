package com.trycore.evmTracker.infrastructure.persistence;

import com.trycore.evmTracker.domain.model.Activity;
import com.trycore.evmTracker.domain.repository.ActivityRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryActivityRepository implements ActivityRepository {

    private final Map<Long, Activity> activities = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public Activity save(Activity activity) {
        if (activity.getId() == null) {
            activity.setId(sequence.getAndIncrement());
        }
        activities.put(activity.getId(), activity);
        return activity;
    }

    @Override
    public Optional<Activity> findById(Long id) {
        return Optional.ofNullable(activities.get(id));
    }

    @Override
    public List<Activity> findByProjectId(Long projectId) {
        List<Activity> result = activities.values().stream()
                .filter(activity -> activity.getProjectId().equals(projectId))
                .sorted(Comparator.comparing(Activity::getId))
                .toList();
        return new ArrayList<>(result);
    }

    @Override
    public void deleteById(Long id) {
        activities.remove(id);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        activities.values().removeIf(activity -> activity.getProjectId().equals(projectId));
    }
}
