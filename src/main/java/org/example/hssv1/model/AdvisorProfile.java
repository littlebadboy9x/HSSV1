package org.example.hssv1.model;

import java.sql.Timestamp;

public class AdvisorProfile {
    private int id;
    private CustomUser user;
    private Department department;
    private Major major;
    private String title;
    private String bio;
    private String expertise;
    private String role;
    private String phone;
    private boolean available;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public AdvisorProfile() {
        this.role = "advisor"; // Mặc định là cố vấn thông thường
        this.available = true; // Mặc định là có sẵn
    }

    public AdvisorProfile(int id, CustomUser user, Department department, Major major, 
                         String title, String bio, String expertise, String role,
                         String phone, boolean available, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.user = user;
        this.department = department;
        this.major = major;
        this.title = title;
        this.bio = bio;
        this.expertise = expertise;
        this.role = role;
        this.phone = phone;
        this.available = available;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CustomUser getUser() {
        return user;
    }

    public void setUser(CustomUser user) {
        this.user = user;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    @Override
    public String toString() {
        return user != null ? user.getFullName() : "Unknown Advisor";
    }
}
