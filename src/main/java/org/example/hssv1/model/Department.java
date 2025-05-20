package org.example.hssv1.model;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private int id;
    private String name;
    private String description;
    private boolean isActive;
    private List<Major> majors;

    public Department() {
        this.majors = new ArrayList<>();
    }

    public Department(int id, String name, String description, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.majors = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Major> getMajors() {
        return majors;
    }

    public void setMajors(List<Major> majors) {
        this.majors = majors;
    }

    public void addMajor(Major major) {
        this.majors.add(major);
    }

    @Override
    public String toString() {
        return name;
    }
}
