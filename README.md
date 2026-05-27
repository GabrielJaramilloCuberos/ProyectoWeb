# VigilApp

**Full-stack web application with Angular frontend, Java/Spring Boot backend, PostgreSQL, and Docker-based deployment.**

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)](https://developer.mozilla.org/docs/Web/HTML)
[![Angular](https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.dev/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![pgAdmin](https://img.shields.io/badge/pgAdmin-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.pgadmin.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Docker Compose](https://img.shields.io/badge/Docker_Compose-1D63ED?style=for-the-badge&logo=docker&logoColor=white)](https://docs.docker.com/compose/)

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Frontend (Angular)](#frontend-angular)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
- [Available Services](#available-services)
- [Team](#team)

---

## Overview

**VigilApp** follows a **frontend + backend + database** architecture and is fully containerized with Docker Compose.

The project includes:
- **Frontend**: Angular 21 (SSR) with Tailwind CSS
- **Backend**: Java with Spring Boot
- **Database**: PostgreSQL with automatic initialization via `init.sql`
- **Database Admin UI**: pgAdmin

---

## Tech Stack

| Layer | Technology |
|------|-----------|
| Frontend | Angular 21 · TypeScript · SSR · Tailwind CSS |
| Backend | Java · Spring Boot |
| Database | PostgreSQL 15 |
| DB Admin | pgAdmin 4 |
| Containers | Docker · Docker Compose |

---

## Project Structure

```text
ProyectoWeb/
├── backend/
│   └── vigilapp/         # Spring Boot source code
│       └── Dockerfile
├── docs/                 # Additional documentation
├── frontend/
│   └── vigilapp/         # Angular app (SSR)
│       └── src/app/
│           ├── core/     # Base models/services
│           └── features/ # Role-based modules (auth, coordinator, etc.)
├── .env.example          # Example environment variables
├── .gitignore
├── compose.yml           # Docker Compose configuration
├── init.sql              # Database initialization script
└── rebuild.sh            # Fast rebuild helper script
```

---

## Frontend (Angular)

### Technical architecture

- Angular 21 application with SSR.
- Standalone components organized by feature modules.
- Typed domain models in `src/app/core/models`.
- Main routing in `src/app/app.routes.ts`.

### Recent frontend additions

- Coordinator dashboard mounted at `/coordinator`, with live monitoring preserved at `/coordinator/live` and reports at `/coordinator/analytics`.
- Coordinator home uses reusable SVG actions and a simplified dashboard layout aligned with the current product UI.
- Shared zone data was centralized in a feature-level catalog so coordinator, professor and heatmap views use the same zone names and metadata.
- The heatmap now supports switching between satellite and simple map bases while keeping the heat overlay active.
- Zone popups now show only the zone name and the number of accidents.
- Professor incident report now uses mobile-first single-column controls, shared toast notifications, and redirects back to the professor home after submission.
- Shared UI components were normalized to standalone form in `src/app/shared/ui` to keep global and local feature components consistent with the architecture.

---


## Prerequisites

Make sure you have installed:

- [Docker](https://docs.docker.com/get-docker/)
- [Git](https://git-scm.com/)
- [Node.js 20+](https://nodejs.org/) (only for local frontend development outside Docker)
- [Angular CLI](https://angular.dev/tools/cli) (optional for local development)

---

## Configuration

1. **Clone the repository:**

```bash
git clone https://github.com/SalomonAvila/ProyectoWeb.git
cd ProyectoWeb
```

2. **Create your environment file** from the example:

```bash
cp .env.example .env
```

3. **Edit `.env`** with your values:

```env
# PostgreSQL
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_strong_password
POSTGRES_DB=vigilapp_db
POSTGRES_PORT=5432

# pgAdmin
PGADMIN_EMAIL=admin@example.com
PGADMIN_PASSWORD=admin123
PGADMIN_PORT=5050

# Spring Boot
SPRING_PORT=8080

# Frontend (Angular SSR)
FRONTEND_PORT=4000
```

---

## Running the Project

**Build and start all services:**

```bash
docker compose up --build
```

**Run in detached mode:**

```bash
docker compose up --build -d
```

**Quick rebuild (using included helper script):**

```bash
bash rebuild.sh
```

**Stop all services:**

```bash
docker compose down
```

**Stop and remove volumes (full cleanup):**

```bash
docker compose down -v
```

---

## Available Services

Once containers are running, you can access:

| Service | URL | Description |
|----------|-----|-------------|
| Frontend | `http://localhost:${FRONTEND_PORT}` | Angular SSR application |
| Backend API | `http://localhost:${SPRING_PORT}` | Spring Boot REST API |
| pgAdmin | `http://localhost:${PGADMIN_PORT}` | PostgreSQL admin interface |
| PostgreSQL | `localhost:${POSTGRES_PORT}` | Direct database access |

> Ports depend on values defined in your `.env` file.

---

## Team

Developed by:

| Member | GitHub |
|-----------|--------|
| Salomon Avila | [@SalomonAvila](https://github.com/SalomonAvila) |
| Gabriel Jaramillo | [@GabrielJaramilloCuberos](https://github.com/GabrielJaramilloCuberos) |
| Valeria Herrera | [@valerrera](https://github.com/valerrera) |
| Ana Maria Romero | [@romerocana](https://github.com/romerocana) |

---
