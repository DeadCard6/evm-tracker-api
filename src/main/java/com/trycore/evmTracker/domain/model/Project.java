package com.trycore.evmTracker.domain.model;

public class Project {

    private Long id;
    private final String name;

    public Project(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name no puede estar vacío");
        }
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
}