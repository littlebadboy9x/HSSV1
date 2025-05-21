package org.example.hssv1.model;

import java.sql.Timestamp;
import javax.persistence.*;

@Entity
@Table(name = "advisor_profiles")
public class AdvisorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private CustomUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id") // Can be null if admin is system-wide
    private Department department; // Department the advisor belongs to, if any

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id") // Can be null
    private Major major; // Major/specialty of the advisor

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdvisorRole role = AdvisorRole.ADVISOR; // Default role

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String title;
    private String expertise;
    private String phone;
    private boolean available;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public AdvisorProfile() {
        this.role = AdvisorRole.ADVISOR; // Mặc định là cố vấn thông thường
        this.available = true; // Mặc định là có sẵn
    }

    public AdvisorProfile(CustomUser user, AdvisorRole role) {
        this.user = user;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public AdvisorRole getRole() {
        return role;
    }

    public void setRole(AdvisorRole role) {
        this.role = role;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
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
        return AdvisorRole.ADMIN.equals(role);
    }

    @Override
    public String toString() {
        return user != null ? user.getFullName() : "Unknown Advisor";
    }

    public enum AdvisorRole {
        ADVISOR, // Ban tư vấn
        ADMIN    // Admin hệ thống
    }
}
