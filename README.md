# 🚀 AI GitHub Code Reviewer

> An intelligent backend system that automatically reviews GitHub Pull Requests using AI and suggests improvements, best practices, and optimizations.

## 🧠 Overview

Manual code reviews are time-consuming and inconsistent.
This project automates the process by integrating **GitHub Webhooks + AI**, providing real-time feedback on pull requests.

## ⚡ Key Features

* 🤖 AI-powered code analysis
* 🔗 GitHub Pull Request integration
* ⚡ Real-time webhook processing
* 📊 Structured feedback (bugs, improvements, best practices)
* 🔐 Secure configuration using environment variables
* 🧩 Scalable Spring Boot architecture

## 🏗️ System Architecture

Developer → GitHub PR → Webhook → Spring Boot Backend → AI Model → Review → GitHub Comment

## 🔄 How It Works

1. Developer creates a Pull Request
2. GitHub sends webhook event
3. Backend receives and processes event
4. Fetches changed code using GitHub API
5. Sends code to AI model (OpenAI / Gemini / Ollama)
6. AI analyzes and generates suggestions
7. Backend posts comments on PR

## 🛠️ Tech Stack

| Layer       | Technology                   |
| ----------- | ---------------------------- |
| Backend     | Java, Spring Boot            |
| AI          | OpenAI API / Gemini / Ollama |
| Database    | H2 / MySQL                   |
| Integration | GitHub API + Webhooks        |
| Build Tool  | Maven                        |


## 📁 Project Structure


src/main/java/com/aashi/aicodereviewer
│
├── controller   → Handles webhook requests
├── service      → AI processing logic
├── repository   → Database operations
├── model        → Data entities


## 🚀 Getting Started

### 1️⃣ Clone Repository

```
git clone https://github.com/aashijainn01/ai-github-code-reviewer.git
cd ai-github-code-reviewer
```

### 2️⃣ Configure Environment Variables

```
OPENAI_API_KEY=your_api_key
GITHUB_TOKEN=your_token
MAIL_PASSWORD=your_password
GITHUB_WEBHOOK_SECRET=your_secret
```

### 3️⃣ Run Application

```
mvn spring-boot:run
```


## 🔐 Security Best Practices

* No API keys stored in code
* Uses environment variables for secrets
* Sensitive files excluded via `.gitignore`


## 📌 Use Cases

* Automating code reviews in teams
* Improving code quality in real-time
* Reducing developer workload
* Enhancing learning for beginners


## 🌟 Future Enhancements

* 🔹 Web dashboard UI
* 🔹 Multi-repository support
* 🔹 Advanced AI feedback formatting
* 🔹 Code scoring system


## 👨‍💻 Author

**Aashi Jain**
B.Tech IT | Java Backend AI Developer


## ⭐ Show Your Support

If you like this project, give it a ⭐ on GitHub!
