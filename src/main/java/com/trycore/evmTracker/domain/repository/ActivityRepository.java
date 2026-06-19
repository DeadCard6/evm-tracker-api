package com.trycore.evmTracker.domain.repository;

import com.trycore.evmTracker.domain.model.Activity;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository {
    Activity save(Activity activity);
    Optional<Activity> findById(Long id);
    List<Activity> findByProjectId(Long projectId);
    void deleteById(Long id);
    void deleteByProjectId(Long projectId);
}
