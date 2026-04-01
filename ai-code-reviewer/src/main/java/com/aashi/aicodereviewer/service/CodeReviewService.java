package com.aashi.aicodereviewer.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aashi.aicodereviewer.model.PullRequestEvent;
import com.aashi.aicodereviewer.model.ReviewHistory;
import com.aashi.aicodereviewer.repository.ReviewHistoryRepository;
@Service
public class CodeReviewService {

    private final GithubService githubService;
    private final AIService aiService;
    private static final Logger log = LoggerFactory.getLogger(CodeReviewService.class);
    private final NotificationService notificationService;
    private final ReviewHistoryRepository reviewHistoryRepository;
    
    
    public CodeReviewService(GithubService githubService,
            AIService aiService,
            ReviewHistoryRepository reviewHistoryRepository,
            NotificationService notificationService) {
this.githubService = githubService;
this.aiService = aiService;
this.reviewHistoryRepository = reviewHistoryRepository;
this.notificationService = notificationService;
}

   
    
    public void processPullRequest(PullRequestEvent event) {

        log.info("📦 Processing Pull Request...");

        String repo = event.getRepository().getFull_name();
        int prNumber = event.getPull_request().getNumber();

        log.info("📁 Repository: {}", repo);
        log.info("🔢 PR Number: {}", prNumber);
        githubService.postInlineComment(
                repo,
                prNumber,
                "README.md",   // file name in your PR
                5,             // line number (change if needed)
                "⚠️ Test inline comment from AI"
            );
        
        
        ReviewHistory latestReview = reviewHistoryRepository
                .findTopByRepoNameAndPrNumberOrderByCreatedAtDesc(repo, prNumber)
                .orElse(null);

        if (latestReview != null && latestReview.getCreatedAt() != null) {
            LocalDateTime twoMinutesAgo = LocalDateTime.now().minusMinutes(2);

            if (latestReview.getCreatedAt().isAfter(twoMinutesAgo)) {
                log.warn("⏳ Rate limit applied. Skipping duplicate review for repo={} pr={}", repo, prNumber);
                return;
            }
        }
        
       
        
       

        log.info("📥 Fetching PR code...");
        String code = githubService.getPullRequestCode(repo, prNumber);

        if (code == null || code.isEmpty()) {
            log.warn("⚠️ No code changes found!");
            return;
        }
        
     // ✅ Skip very large code
        if (code.length() > 2000) {
        	log.warn("⚠️ Code too large to review: {}", code.length());
            githubService.postReviewComment(
                repo,
                prNumber,
                "⚠️ Skipped AI review because changed code is too large (" + code.length() + " chars). Please split the PR into smaller changes."
            );
            return;
        }
        
        String language = githubService.detectPrimaryLanguage(repo, prNumber);
        log.info("🌐 Detected language: {}", language);


        log.info("🤖 Sending code to AI...");
        String review = null;
        int attempts = 0;
        int maxAttempts = 3;

        while (attempts < maxAttempts) {
            try {
                log.info("🤖 AI attempt {}", attempts + 1);

                review = aiService.reviewCode(code,language);

                if (review != null && !review.trim().isEmpty()) {
                    break;
                }

            } catch (Exception e) {
                log.error("❌ AI failed on attempt {}", attempts + 1, e);
            }

            attempts++;
        }
        
        String summary = githubService.getPrSummary(repo, prNumber);
        
      
        
        String status = "COMPLETED";

        if (review == null || review.trim().isEmpty()) {
            review = "⚠️ AI failed after multiple attempts.";
            status = "FAILED";
        }
        
     
        log.info("💬 Posting review comment...");
        String finalComment = summary + "\n\n" + review;

        githubService.postReviewComment(repo, prNumber, finalComment);
        
        ReviewHistory reviewHistory = new ReviewHistory(
                repo,
                prNumber,
                review,
                status,
                LocalDateTime.now()
        );
       

        reviewHistoryRepository.save(reviewHistory);

        log.info("✅ Review completed and saved to DB!");
        notificationService.sendReviewNotification(repo, prNumber, status, finalComment);
      
    }
}