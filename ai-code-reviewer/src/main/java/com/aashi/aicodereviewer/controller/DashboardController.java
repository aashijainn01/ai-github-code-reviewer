package com.aashi.aicodereviewer.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.aashi.aicodereviewer.model.ReviewHistory;
import com.aashi.aicodereviewer.repository.ReviewHistoryRepository;

@Controller
public class DashboardController {

    private final ReviewHistoryRepository reviewHistoryRepository;

    public DashboardController(ReviewHistoryRepository reviewHistoryRepository) {
        this.reviewHistoryRepository = reviewHistoryRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<ReviewHistory> reviews = reviewHistoryRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("reviews", reviews);
        return "dashboard";
    }
}
