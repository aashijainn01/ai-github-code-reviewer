package com.aashi.aicodereviewer.controller;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.aashi.aicodereviewer.model.PullRequestEvent;
import com.aashi.aicodereviewer.service.CodeReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final CodeReviewService reviewService;
    private final ObjectMapper objectMapper;

    @Value("${github.webhook.secret}")
    private String webhookSecret;

    public WebhookController(CodeReviewService reviewService, ObjectMapper objectMapper) {
        this.reviewService = reviewService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/github")
    public String handleGithubWebhook(@RequestBody String payload,
                                      @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {

        log.info("🚀 Webhook triggered!");

        if (signature == null || !isValidSignature(payload, signature)) {
            log.error("❌ Invalid webhook signature!");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid signature");
        }

        try {
            PullRequestEvent event = objectMapper.readValue(payload, PullRequestEvent.class);

            if (!"opened".equals(event.getAction()) &&
                !"synchronize".equals(event.getAction())) {

                log.info("⏭️ Ignoring event: {}", event.getAction());
                return "Ignored";
            }

            if (event.getRepository() == null || event.getPull_request() == null) {
                log.warn("⚠️ Invalid webhook payload");
                return "Invalid payload";
            }

            log.info("📁 Repository: {}", event.getRepository().getFull_name());
            log.info("🔢 PR Number: {}", event.getPull_request().getNumber());

            reviewService.processPullRequest(event);

            return "Webhook received successfully";

        } catch (Exception e) {
            log.error("❌ Error processing webhook", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payload");
        }
    }

    private boolean isValidSignature(String payload, String signatureHeader) {
        try {
            String expectedSignature = "sha256=" + hmacSha256(payload, webhookSecret);
            return expectedSignature.equals(signatureHeader);
        } catch (Exception e) {
            log.error("❌ Error validating signature", e);
            return false;
        }
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);

        byte[] hash = mac.doFinal(data.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}