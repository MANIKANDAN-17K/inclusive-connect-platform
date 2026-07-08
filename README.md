# Inclusive Connect

> **An Accessible Professional Networking Platform Connecting Differently-Abled Talent with Inclusive Employers.**

Inclusive Connect is a full-stack web application designed to empower differently-abled professionals by providing an accessible platform for networking, job discovery, and professional growth. The platform enables candidates to connect with employers, showcase their skills, apply for jobs, communicate in real time, and access accessibility-focused features.

This project is being developed following professional software engineering practices, including requirement analysis, system design, RESTful API development, clean architecture, security best practices, and comprehensive documentation.

---

## Vision

To create an inclusive digital ecosystem where differently-abled individuals can confidently build professional networks, discover career opportunities, and connect with organizations committed to inclusive hiring.

---

## Key Features

### Authentication & Security

* User Registration
* Secure Login
* JWT Authentication
* Refresh Tokens
* Email Verification
* Forgot & Reset Password
* Role-Based Access Control (RBAC)

### Candidate Portal

* Professional Profile
* Resume Upload
* Skills Management
* Education & Experience
* Job Search
* Job Applications
* Saved Jobs

### Employer Portal

* Company Profile
* Job Posting & Management
* Applicant Tracking
* Candidate Search
* Employer Verification

### Professional Networking

* Connection Requests
* Professional Connections
* User Discovery

### Real-Time Communication

* One-to-One Messaging
* Live Notifications
* Typing Indicators
* Read Receipts

### Accessibility Features

* WCAG 2.1 Inspired Design
* High Contrast Mode
* Keyboard Navigation
* Adjustable Font Size
* Screen Reader Support
* Responsive User Interface

### Administration

* User Management
* Employer Verification
* Platform Moderation
* Dashboard & Reports

---

# Technology Stack

## Frontend

* Angular
* TypeScript
* Angular Material
* Tailwind CSS
* RxJS

## Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* JWT
* MapStruct
* Bean Validation
* Swagger/OpenAPI

## Database

* MySQL

## Real-Time Communication

* Spring WebSocket
* STOMP

## DevOps & Tools

* Maven
* Git & GitHub
* Docker (Planned)
* Docker Compose (Planned)
* GitHub Actions (Planned)

---

# Project Structure

```text
inclusive-connect/
│
├── backend/                 # Spring Boot REST API
│
├── frontend/                # Angular Application
│
├── database/                # SQL Scripts & Migrations
│
├── docs/                    # Software Engineering Documents
│   ├── SRS
│   ├── Architecture
│   ├── API Design
│   ├── UML Diagrams
│   └── Testing
│
├── .gitignore
├── docker-compose.yml
├── README.md
└── LICENSE
```

---

# Software Engineering Process

This project follows a structured software development lifecycle.

* Requirement Analysis
* Software Requirement Specification (SRS)
* Use Case Analysis
* Database Design
* REST API Design
* System Architecture
* UI/UX Design
* Backend Development
* Frontend Development
* Testing
* Deployment
* Documentation

---

# Development Roadmap

### Phase 1

* Project Setup
* Authentication
* Authorization
* Email Verification

### Phase 2

* Candidate Profile
* Education
* Experience
* Resume Upload

### Phase 3

* Employer Module
* Company Profile

### Phase 4

* Job Management
* Job Applications

### Phase 5

* Professional Networking

### Phase 6

* Real-Time Messaging

### Phase 7

* Notification System

### Phase 8

* Accessibility Features

### Phase 9

* Admin Dashboard

### Phase 10

* Deployment & DevOps

---

# Architecture

```text
                Angular Frontend
                       │
             REST API / WebSocket
                       │
              Spring Boot Backend
                       │
        Service → Repository → MySQL
```

---

# Current Status

**Project Status:** 🚧 Active Development

### Completed

* Software Requirement Specification (SRS)
* System Architecture
* Database Design
* REST API Design
* UI/UX Planning

### In Progress

* Backend Development

### Planned

* Frontend Development
* Testing
* Deployment
* Documentation

---

# Getting Started

## Clone Repository

```bash
git clone https://github.com/MANIKANDAN-17K/inclusive-connect.git
```

## Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

## Frontend

```bash
cd frontend
npm install
ng serve
```

---

# Documentation

Project documentation will be maintained in the `docs/` directory and includes:

* Software Requirement Specification (SRS)
* Architecture Design
* Database Design
* API Documentation
* UML Diagrams
* Testing Documentation

---

# Contributing

Contributions, suggestions, and feedback are welcome. Please create an issue or submit a pull request before making major changes.

---

# License

This project is developed for educational, learning, and portfolio purposes.

---

## Author

**Manikandan K**

Bachelor of Engineering in Computer Science and Engineering

Building software with a focus on accessibility, scalability, and clean architecture.

