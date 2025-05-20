package org.example.hssv1.model;

public class Major {
    private int id;
    private String name;
    private String description;
    private boolean isActive;
    private Department department;

    public Major() {
    }

    public Major(int id, String name, String description, boolean isActive, Department department) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.department = department;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return name;
    }
}
