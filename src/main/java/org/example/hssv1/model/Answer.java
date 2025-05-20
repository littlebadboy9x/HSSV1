package org.example.hssv1.model;

import java.sql.Timestamp;

/**
 * Mô hình Câu trả lời
 */
public class Answer {
    private int id;
    private Question question;
    private CustomUser user;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    /**
     * Constructor mặc định
     */
    public Answer() {
    }
    
    /**
     * Constructor với các tham số cơ bản
     */
    public Answer(int id, Question question, CustomUser user, String content, 
                 Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.question = question;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter và Setter
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public CustomUser getUser() {
        return user;
    }
    
    public void setUser(CustomUser user) {
        this.user = user;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
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
}
