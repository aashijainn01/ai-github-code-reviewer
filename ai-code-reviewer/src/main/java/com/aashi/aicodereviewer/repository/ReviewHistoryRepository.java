package com.aashi.aicodereviewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aashi.aicodereviewer.model.ReviewHistory;

public interface ReviewHistoryRepository extends JpaRepository<ReviewHistory, Long> {

    List<ReviewHistory> findAllByOrderByCreatedAtDesc();
    
    Optional<ReviewHistory> findTopByRepoNameAndPrNumberOrderByCreatedAtDesc(String repoName, int prNumber);
}