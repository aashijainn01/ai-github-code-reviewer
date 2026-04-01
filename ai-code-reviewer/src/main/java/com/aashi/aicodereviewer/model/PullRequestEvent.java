package com.aashi.aicodereviewer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestEvent {

    private String action;
    private PullRequest pull_request;
    private Repository repository;

    // ✅ ADD THIS (IMPORTANT)
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public PullRequest getPull_request() {
        return pull_request;
    }

    public void setPull_request(PullRequest pull_request) {
        this.pull_request = pull_request;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}