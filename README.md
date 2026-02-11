# Fuchacz
### Version: Pre-alpha 1.0
  > The project is under active development. Breaking changes, incomplete features and missing documentation are expected.

**Fuchacz** is an application for easy searching of contract-for-task jobs. It helps employers find workers through automated features and helps job seekers with a friendly swipe interface.

## Table of contents
- [Project structure](#project-structure)
- [Features](#features)
- [Preview](#preview)
- [Contact](#contact)

## SETUP

### Quick setup
1. Clone the repository `git clone repo-url`
2. Create `secrets` folder, `certs` folder and `.env` file
3. Update admin panel `git submodule update --init --recursive`
4. Run containers with `docker compose --profile init up --build`
5. Go to mobile folder `cd mobile`
6. Run mobile app with `npm start`

### Full english setup: [SETUP-en.md](./SETUP-en.md)

### Full polish setup: [SETUP-pl.md](./SETUP-pl.md)

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
<div align="center">

| Area | Technology |
|:---:|:---:|
| API | <img height="40" src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><img height="40" src="https://img.shields.io/badge/Java%2021-FF7900?style=for-the-badge&logo=openjdk&logoColor=white"> |
| Mobile application | <img height="40" src="https://img.shields.io/badge/React%20Native-61DAFB?style=for-the-badge&logo=react&logoColor=black"><img height="40" src="https://img.shields.io/badge/typescript-3178C6?style=for-the-badge&logo=typescript&logoColor=white"><img height="40" src="https://img.shields.io/badge/stylesheet-663399?style=for-the-badge"> |
| Admin panel (SPA) | <img height="40" src="https://img.shields.io/badge/Angular%2021-A6120d?style=for-the-badge&logo=angular&logoColor=white"><img height="40" src="https://img.shields.io/badge/typescript-3178C6?style=for-the-badge&logo=typescript&logoColor=white"><img height="40" src="https://img.shields.io/badge/SCSS-CC6699?style=for-the-badge&logo=sass&logoColor=white"> |
| SSR | <img height="40" src="https://img.shields.io/badge/ejs-B4CA65?style=for-the-badge&logo=ejs&logoColor=black"><img height="40" src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"><img height="40" src="https://img.shields.io/badge/css-663399?style=for-the-badge&logo=css&logoColor=white"> |
| Authorization | <img height="40" src="https://img.shields.io/badge/google%20oauth%202.0-663399?style=for-the-badge&logo=google&logoColor=white"><img height="40" src="https://img.shields.io/badge/spring%20security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> |
| Database | <img height="40" src="https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"> |
| Additional storage (tokens, codes) | <img height="40" src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white"> |
| Cloud storage | <img height="40" src="https://img.shields.io/badge/cloudflare-F38020?style=for-the-badge&logo=cloudflare&logoColor=white"> |
| Scripts | <img height="40" src="https://img.shields.io/badge/bash-4EAA25?style=for-the-badge&logo=gnubash&logoColor=white"><img height="40" src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white"> |
| Infrastructure | <img height="40" src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"><img height="40" src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white"> |

</div>

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

### Mobile App
<div align="center">

| App entry | Swipe home screen | Job creation |
|-----------|------------------|--------------|
| <img src="images/welcome.gif" width="300"/> | <img src="images/swipe.gif" width="300"/> | <img src="images/create-offer.gif" width="300"/> |
  
</div>

### Landing page
![0210 (1)](https://github.com/user-attachments/assets/7de3d027-8115-46d1-a0b7-177c1eedb853)

### Admin panel
![0210 (1)(1)](https://github.com/user-attachments/assets/d9994dd2-e3c6-42d6-8e60-571c6d49dfe0)

## Contact

If you have questions or issues, contact the repository owners or open a GitHub issue.

### Created by Jan Gasztold, Maciej Adamski and Mikołaj Kalejta
