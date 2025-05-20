package org.example.hssv1.model;

import java.sql.Timestamp;

public class CustomUser {
    private int id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private boolean isStaff;
    private boolean isSuperuser;
    private Timestamp dateJoined;
    private Timestamp lastLogin;
    private String userType; // STUDENT, ALUMNI, PARENT, HIGHSCHOOL, ADVISOR, ADMIN

    public CustomUser() {
    }

    public CustomUser(int id, String username, String password, String email, String firstName, String lastName,
                     boolean isActive, boolean isStaff, boolean isSuperuser, Timestamp dateJoined,
                     Timestamp lastLogin, String userType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.isStaff = isStaff;
        this.isSuperuser = isSuperuser;
        this.dateJoined = dateJoined;
        this.lastLogin = lastLogin;
        this.userType = userType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
    }

    public boolean isSuperuser() {
        return isSuperuser;
    }

    public void setSuperuser(boolean superuser) {
        isSuperuser = superuser;
    }

    public Timestamp getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Timestamp dateJoined) {
        this.dateJoined = dateJoined;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
