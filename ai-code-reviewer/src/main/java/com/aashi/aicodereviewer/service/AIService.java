package com.aashi.aicodereviewer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIService {

	@Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String reviewCode(String code, String language){

        try {

//           
        	 String url = "https://api.openai.com/v1/chat/completions";

        	 String prompt;

        	 if ("Java".equalsIgnoreCase(language)) {
        	     prompt = """
        	     You are a senior Java backend code reviewer.

        	     Review the following Java code changes.
        	     Focus on:
        	     - object-oriented design
        	     - null safety
        	     - Spring Boot best practices
        	     - exception handling
        	     - readability
        	     - performance

        	    Respond STRICTLY in this format:

🤖 AI Code Review

🔴 Critical Issues:
- ...
- If none, write: No critical issues found

🟡 Warnings:
- ...
- If none, write: No warnings found

🟢 Suggestions:
- ...
- If none, write: No suggestions found

🛠 Auto-Fix Suggestions:
- Provide exact replacement snippets where useful
- Use format:

  Replace:
  <old code>

  With:
  <new code>

- If none, write: No auto-fix suggestions

Code:
""" + code;

        	 } else if ("Python".equalsIgnoreCase(language)) {
        	     prompt = """
        	     You are a senior Python code reviewer.

        	     Review the following Python code changes.
        	     Focus on:
        	     - readability
        	     - PEP8 style
        	     - exception handling
        	     - function design
        	     - performance
        	     - maintainability

        	   Respond STRICTLY in this format:

🤖 AI Code Review

🔴 Critical Issues:
- ...
- If none, write: No critical issues found

🟡 Warnings:
- ...
- If none, write: No warnings found

🟢 Suggestions:
- ...
- If none, write: No suggestions found

🛠 Auto-Fix Suggestions:
- Provide exact replacement snippets where useful
- Use format:

  Replace:
  <old code>

  With:
  <new code>

- If none, write: No auto-fix suggestions

Code:
""" + code;

        	 } else if ("JavaScript".equalsIgnoreCase(language)) {
        	     prompt = """
        	     You are a senior JavaScript code reviewer.

        	     Review the following JavaScript code changes.
        	     Focus on:
        	     - async handling
        	     - readability
        	     - variable usage
        	     - code quality
        	     - maintainability
        	     - best practices

        	    Respond STRICTLY in this format:

🤖 AI Code Review

🔴 Critical Issues:
- ...
- If none, write: No critical issues found

🟡 Warnings:
- ...
- If none, write: No warnings found

🟢 Suggestions:
- ...
- If none, write: No suggestions found

🛠 Auto-Fix Suggestions:
- Provide exact replacement snippets where useful
- Use format:

  Replace:
  <old code>

  With:
  <new code>

- If none, write: No auto-fix suggestions

Code:
""" + code;

        	 } else {
        	     prompt = """
        	     You are a senior software engineer and code reviewer.

        	     Review the following code changes.

        	    Respond STRICTLY in this format:

🤖 AI Code Review

🔴 Critical Issues:
- ...
- If none, write: No critical issues found

🟡 Warnings:
- ...
- If none, write: No warnings found

🟢 Suggestions:
- ...
- If none, write: No suggestions found

🛠 Auto-Fix Suggestions:
- Provide exact replacement snippets where useful
- Use format:

  Replace:
  <old code>

  With:
  <new code>

- If none, write: No auto-fix suggestions

Code:
""" + code;
        	 }
        	 

            String requestBody = """
            {
              "contents": [{
                "parts":[{"text": "%s"}]
              }]
            }
            """.formatted(prompt.replace("\"", "\\\""));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            System.out.println("🤖 AI RAW RESPONSE:");
            System.out.println(response.getBody());

            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI failed to generate review.";
        }
    }
}