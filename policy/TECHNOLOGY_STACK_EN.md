# Technology Stack

## Overview

The project is a comprehensive job marketplace platform built around a **mobile-first architecture**, where the mobile application serves as the primary and required interface for end users. The system enables users to create job offers, browse opportunities through an intuitive swipe-based interface, and operate in dual roles as both employers and workers.

The platform consists of several independent but interconnected components: a mobile application, a backend API, an administrative panel, a server-side rendered website, databases, cloud storage, and supporting infrastructure. The architecture emphasizes scalability, maintainability, security, automation, and clear project organization.

---

# Components

## Mobile Application (SPA Main Product)

The mobile application is the central element of the ecosystem and the primary way users interact with the platform.

### Technologies

* React Native
* TypeScript
* Stylesheet (CSS-in-JS)
* SwiftUI

### Responsibilities

* Swipe-based browsing of job offers (**Swipe UI**)
* Offer creation and management
* User profile management
* Job application management
* Dual-role support (worker and employer)
* Secure job lifecycle handling
* Communication with backend API
* Home Screen widgets on iOS using SwiftUI
* Authentication and authorization handling

## API

The backend acts as the central business layer responsible for all application logic, authorization, validation, and communication with data storage systems.

### Technologies

* Spring Boot
* Java 21
* Spring Security

### Responsibilities

* Business logic implementation
* User authentication and authorization
* Custom Spring Security implementation
* Google OAuth 2.0 integration
* User profile management
* Offer management
* Job state management
* Role-based access control
* Communication with PostgreSQL
* Communication with Redis
* Integration with Cloudflare storage
* API access for all frontend applications

## Administrative Panel (SPA)

The administrative panel is a dedicated web application available exclusively to administrators. It simplifies system monitoring and database management without the need to manually execute SQL queries.

### Technologies

* Angular 21
* TypeScript
* SCSS

### Responsibilities

* User management
* Offer moderation
* Job monitoring
* Administrative operations
* Database content management
* System oversight and maintenance

---

## Landing Page (SSR)

The website is designed primarily for presentation, SEO, indexing, and account recovery operations.

### Technologies

* EJS
* JavaScript
* CSS

### Responsibilities

* Product presentation
* SEO optimization
* Search engine indexing
* Password reset flows
* Public information pages
* Marketing and onboarding content

## Authorization

* Google OAuth 2.0
* Spring Security
* JWT (JSON Web Tokens)

### Features

* Custom Google OAuth 2.0 authorization flow
* Custom Spring Security implementation
* JWT-based authentication
* Access token and refresh token support
* Password reset authorization codes
* Role-based permissions
* User/Admin permission separation
* TLS encrypted communication

## Data Layer

### PostgreSQL

Persistent storage for:

* Users
* Job offers
* Jobs
* Application states
* System metadata

### Redis

Fast temporary storage for:

* Refresh tokens
* Reset codes
* Authentication codes

### Cloud Storage

Storage of:

* Images
* CV documents
* Employment contracts
* Other uploaded files

## Infrastructure

### Docker Compose

Provides:

* Consistent development environments
* Reproducible deployments
* Service orchestration
* Simplified local setup

### NGINX

Provides:

* Reverse proxy functionality
* Traffic routing
* TLS termination
* Static resource delivery
* Infrastructure abstraction layer

### TLS

Ensures:

* Encrypted communication
* Secure data transfer
* Protection of authentication data

## Development Tooling and Automation

### Technologies

* Python
* Bash

### Responsibilities

Automation of:

* Database seeding
* Environment setup
* Test execution
* Deployment support
* Development workflows
* Data management operations

### Quality Assurance

* Automated test coverage reporting
* Coverage dashboards
* CI/CD quality monitoring
* Automated code verification

### Additional Tooling

* Spotless formatting
* Automated pipelines
* Main branch protection
* Consistent code style enforcement

# Architecture Summary

| Component         | Technology                        | Purpose                           |
| ----------------- | --------------------------------- | --------------------------------- |
| Mobile App        | React Native, TypeScript, SwiftUI | Main user-facing platform         |
| Backend API       | Spring Boot, Java 21              | Business logic and authorization  |
| Admin Panel       | Angular, TypeScript, SCSS         | Administrative operations         |
| Website           | EJS SSR, JavaScript               | SEO, presentation, password reset |
| Database          | PostgreSQL                        | Persistent application data       |
| Cache & Tokens    | Redis                             | Temporary authorization data      |
| Cloud Storage     | Cloudflare                        | Files, CVs, contracts, images     |
| Infrastructure    | Docker, NGINX, TLS                | Deployment and networking         |
| Automation        | Bash, Python                      | Development and maintenance       |
| Quality Assurance | Codecov, Spotless, CI/CD          | Code quality and reporting        |

---

# Documentation and Maintainability

The project places strong emphasis on maintainability and developer onboarding through extensive documentation.

This approach improves project scalability, onboarding speed, and long-term maintainability.

---

# Simplified Project Structure

```text
project-root/
│
├── docker-compose.yml
│
├── mobile/                    # Mobile application
│
├── spring-boot-service/       # Main API
│
├── admin-panel/               # Administrative panel
│
├── website/                   # landing page
│
├── nginx/
│
├── scripts/
│
├── certs/
│
├── secrets/
│
...
```

## High-Level Architecture

```text
Website    Mobile App (React Native)     Administrativen Panel
   └─────────────────────┼─────────────────────────┘
                         ▼
                   Spring Boot API
                         │
              ┌──────────┼──────────┐
              ▼          ▼          ▼
             PostgreSQL Redis   Cloudflare
```