package com.aashi.aicodereviewer.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class GithubService {

    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final OkHttpClient client = new OkHttpClient();
    private static final Logger log = LoggerFactory.getLogger(GithubService.class);
    
    
    public String getPrSummary(String repo, int prNumber) {
        try {
            String url = "https://api.github.com/repos/" + repo + "/pulls/" + prNumber;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();

            int filesChanged = (int) body.get("changed_files");
            int additions = (int) body.get("additions");
            int deletions = (int) body.get("deletions");

            return "📊 PR Summary:\n\n" +
                    "- Files changed: " + filesChanged + "\n" +
                    "- Lines added: " + additions + "\n" +
                    "- Lines removed: " + deletions;

        } catch (Exception e) {
            log.error("❌ Failed to fetch PR summary", e);
            return "⚠️ Could not generate PR summary";
        }
    }
    
    public String detectPrimaryLanguage(String repo, int prNumber) {
        String code = getPullRequestCode(repo, prNumber);

        if (code.contains("public class") || code.contains("@Service") || code.contains("System.out.println")) {
            return "Java";
        } else if (code.contains("def ") || code.contains("print(")) {
            return "Python";
        } else if (code.contains("function ") || code.contains("console.log(") || code.contains("let ")) {
            return "JavaScript";
        }

        return "Unknown";
    }

    // 🔹 Fetch PR code (extract only patches)
    public String getPullRequestCode(String repo, int prNumber) {

        try {
            String url = "https://api.github.com/repos/" + repo + "/pulls/" + prNumber + "/files";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + githubToken);
            headers.set("Accept", "application/vnd.github+json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            StringBuilder patches = new StringBuilder();

            for (JsonNode file : root) {
                if (file.has("patch")) {
                    patches.append(file.get("patch").asText()).append("\n\n");
                }
            }

            System.out.println("✅ Extracted patches:");
            System.out.println(patches.toString());

            return patches.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // 🔹 Post AI review comment on PR
    public void postReviewComment(String repo, int prNumber, String review) {

        try {
            String url = "https://api.github.com/repos/" + repo + "/issues/" + prNumber + "/comments";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + githubToken);
            headers.set("Accept", "application/vnd.github+json");
            headers.set("Content-Type", "application/json");

            String safeReview = review.replace("\"", "\\\"");

            String body = "{\"body\": \"" + safeReview + "\"}";

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            restTemplate.postForObject(url, entity, String.class);

            System.out.println("🚀 Comment posted on PR successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
    public void postInlineComment(String repo, int prNumber, String filePath, int line, String comment) {

        try {
            String url = "https://api.github.com/repos/" + repo + "/pulls/" + prNumber + "/comments";

            String body = """
            {
              "body": "%s",
              "commit_id": "%s",
              "path": "%s",
              "line": %d
            }
            """.formatted(comment, "HEAD", filePath, line);
            
            RequestBody requestBody = RequestBody.create(
            	    body,
            	    MediaType.parse("application/json")
            	);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + githubToken)
                    .addHeader("Accept", "application/vnd.github+json")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            System.out.println("Inline comment response: " + response.body().string());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}