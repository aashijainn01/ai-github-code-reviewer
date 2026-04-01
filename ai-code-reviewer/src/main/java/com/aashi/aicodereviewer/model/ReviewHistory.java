package com.aashi.aicodereviewer.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ReviewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String repoName;

    private int prNumber;

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    private String status;

    private LocalDateTime createdAt;

    public ReviewHistory() {
    }

    public ReviewHistory(String repoName, int prNumber, String reviewText, String status, LocalDateTime createdAt) {
        this.repoName = repoName;
        this.prNumber = prNumber;
        this.reviewText = reviewText;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public int getPrNumber() {
        return prNumber;
    }

    public void setPrNumber(int prNumber) {
        this.prNumber = prNumber;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}