# Fuchacz
### Version: Pre-alpha 1.0

**Fuchacz** is an application for easy searching of contract-for-task jobs. It helps employers find workers through automated features and helps job seekers with a friendly swipe interface.

> Go to english setup: [SETUP-en.md](./SETUP-en.md)

> Go to polish setup: [SETUP-pl.md](./SETUP-pl.md)

## Table of contents
- [Project structure](#project-structure)
- [Features](#features)
- [Preview](#preview)
- [Contact](#contact)

## Project structure

### Project tree

```
JobFinderApp/
├── docker-compose.yml
├── data.sql
├── spring-boot-service/
│   └── ...
├── mobile/
│   └── ...
├── website/
│   └── ...
├── admin-panel/projekt/
│   └── ...
└── ...
```

### Technology stack
- API: Spring Boot (Java 21)
- Mobile application: React Native, TypeScript, StyleSheet
- Admin panel (SPA): Angular 21, TypeScript, SCSS
- SSR: EJS, JavaScript, CSS
- Database: PostgreSQL
- Additional storage (tokens, codes): Redis
- Cloud storage: Cloudflare
- OAuth 2.0: Google
- Scripts: Bash, Python
- Infrastructure: Docker, NGINX

## Features

- Swipe UI (Mobile) — Intuitive browsing of job offers in the mobile app.
- Offer management (Mobile) — Users can create and manage offers.
- Dual role usage — A user can be both an employer and a worker.
- Job state logic — Careful state management secured for both sides to avoid bias.
- Admin Panel (SPA) — Angular admin panel for easy monitoring and database management.
- Spring Boot API — Java 21 backend API for business logic and database communication.
- Custom Spring Security — Custom, strict backend security.
- SSR Website — EJS-based SSR site for fast rendering and SEO.
- PostgreSQL — Persistent data with a rich structure (users, offers, jobs).
- Redis — Fast storage of tokens/codes with automatic expiration.
- Cloudflare — Storage for images and documents (CVs, contracts).
- Pipeline and spotless — Automated formatting and checks to avoid errors on `main` branch.
- Custom Google authorization — Dedicated authorization process integrated with Google OAuth 2.0.
- User profiles — Required profile data and preferences.
- Role system — Permission separation between users and admins.
- Docker Compose — Consistent containerized runtime environment.
- Data management — Export and import with `data.sql`.
- Hot reload — Faster development with live updates.
- Mobile integration — Dedicated React Native client.
- Web integration — SSR website for presentation and indexing.
- Tooling scripts — Bash/Python automation.
- Proxy/Reverse proxy — NGINX infrastructure layer.
- Admin panel submodule — Separate admin panel as a git submodule.

## Preview
### App entry
<!-- TODO: Add gif -->
### Swipe home screen
<!-- TODO: Add gif -->
### Job creation
<!-- TODO: Add gif -->
### Job Dispatcher
<!-- TODO: Add gif -->
### Landing page
<!-- TODO: Add gif -->
### Admin panel
<!-- TODO: Add gif -->

## Contact

If you have questions or issues, contact the repository owners or open a GitHub issue.

### Created by Jan Gasztold, Maciej Adamski and Mikołaj Kalejta
