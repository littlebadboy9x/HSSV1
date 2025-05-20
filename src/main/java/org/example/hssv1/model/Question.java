package org.example.hssv1.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Mô hình Câu hỏi
 */
public class Question {
    private int id;
    private CustomUser user;
    private String title;
    private String content;
    private Major major;
    private QuestionCategory category;
    private String status;
    private int viewCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<Answer> answers;

    public Question() {
        this.answers = new ArrayList<>();
        this.status = "pending"; // Mặc định là đang chờ
        this.viewCount = 0;
    }

    public Question(int id, CustomUser user, String title, String content, Major major,
                   QuestionCategory category, String status, int viewCount,
                   Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.content = content;
        this.major = major;
        this.category = category;
        this.status = status;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.answers = new ArrayList<>();
    }

    // Getters và Setters
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public QuestionCategory getCategory() {
        return category;
    }

    public void setCategory(QuestionCategory category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
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

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    public boolean isAnswered() {
        return "answered".equals(status);
    }

    public boolean isPending() {
        return "pending".equals(status);
    }
} 