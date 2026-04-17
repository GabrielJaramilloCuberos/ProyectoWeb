# VigilApp

**Aplicación web full-stack con frontend Angular, backend Java/Spring Boot, PostgreSQL y despliegue con Docker.**

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

## Tabla de Contenidos

- [Descripción](#-descripción)
- [Tecnologías](#-tecnologías)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Frontend (Angular)](#-frontend-angular)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración](#️-configuración)
- [Levantar el Proyecto](#-levantar-el-proyecto)
- [Servicios Disponibles](#-servicios-disponibles)
- [Equipo](#-equipo)

---

## Descripción

**VigilApp** es una aplicación web con arquitectura **frontend + backend + base de datos**, contenerizada con Docker Compose.

El proyecto incluye:
- **Frontend** en Angular 21 (SSR) con Tailwind CSS
- **Backend** construido con Java (Spring Boot)
- **Base de datos** PostgreSQL con inicialización automática vía `init.sql`
- **Panel de administración** de base de datos con pgAdmin
- **CI/CD** configurado con GitHub Actions

---

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Frontend | Angular 21 · TypeScript · SSR · Tailwind CSS |
| Backend | Java · Spring Boot |
| Base de datos | PostgreSQL 15 |
| DB Admin | pgAdmin 4 |
| Contenedores | Docker · Docker Compose |
| CI/CD | GitHub Actions |

---

## Estructura del Proyecto

```
ProyectoWeb/
├── .github/
│   └── workflows/        # Pipelines de CI/CD
├── backend/
│   └── vigilapp/         # Código fuente Spring Boot
│       └── Dockerfile
├── docs/                 # Documentación adicional
├── frontend/
│   └── vigilapp/         # App Angular (SSR)
│       └── src/app/
│           ├── core/     # Modelos/servicios base
│           └── features/ # Módulos por rol (auth, coordinator, etc.)
├── .env.example          # Variables de entorno de ejemplo
├── .gitignore
├── compose.yml           # Configuración Docker Compose
├── init.sql              # Script de inicialización de la DB
└── rebuild.sh            # Script de reconstrucción rápida
```

---

## Frontend (Angular)

### Arquitectura técnica

- Aplicación Angular 21 con renderizado SSR.
- Organización por *features* y componentes standalone.
- Modelos tipados en `src/app/core/models`.
- Enrutamiento principal en `src/app/app.routes.ts`.

### Incorporaciones recientes (frontend)

- Módulo de coordinador en la ruta base `/coordinator`.
- Pantalla principal en `/coordinator/home`.
- Reemplazo de “Mapa de Zonas” por **mapa de calor técnico en modo claro**.
- Zonas con indicador visual por nivel de calor (`Baja`, `Media`, `Alta`, `Crítica`).
- Interacción por click en zona para abrir resumen técnico con:
	- total histórico de incidentes,
	- incidentes del último mes,
	- incidentes de la última semana,
	- docente asignado/activo en la zona.
- Diálogo de detalle de zona actualizado para consumo de métricas agregadas.

---

## Requisitos Previos

Asegúrate de tener instalado:

- [Docker](https://docs.docker.com/get-docker/)
- [Git](https://git-scm.com/)
- [Node.js 20+](https://nodejs.org/) (solo para ejecución local del frontend fuera de Docker)
- [Angular CLI](https://angular.dev/tools/cli) (opcional para desarrollo local)

---

## Configuración

1. **Clona el repositorio:**

```bash
git clone https://github.com/SalomonAvila/ProyectoWeb.git
cd ProyectoWeb
```

2. **Crea tu archivo de variables de entorno** a partir del ejemplo:

```bash
cp .env.example .env
```

3. **Edita el archivo `.env`** con tus valores:

```env
# PostgreSQL
POSTGRES_USER=tu_usuario
POSTGRES_PASSWORD=tu_contraseña_segura
POSTGRES_DB=vigilapp_db
POSTGRES_PORT=5432

# pgAdmin
PGADMIN_EMAIL=admin@ejemplo.com
PGADMIN_PASSWORD=admin123
PGADMIN_PORT=5050

# Spring Boot
SPRING_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Frontend (Angular SSR)
FRONTEND_PORT=4000
```

---

## Levantar el Proyecto

**Construir y levantar todos los servicios:**

```bash
docker compose up --build
```

**Levantar en segundo plano:**

```bash
docker compose up --build -d
```

**Reconstruir rápidamente (usando el script incluido):**

```bash
bash rebuild.sh
```

**Detener todos los servicios:**

```bash
docker compose down
```

**Detener y eliminar volúmenes (limpieza total):**

```bash
docker compose down -v
```

---

## Servicios Disponibles

Una vez levantados los contenedores, puedes acceder a:

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Frontend | `http://localhost:${FRONTEND_PORT}` | Aplicación Angular SSR |
| Backend API | `http://localhost:${SPRING_PORT}` | API REST de Spring Boot |
| pgAdmin | `http://localhost:${PGADMIN_PORT}` | Administrador visual de PostgreSQL |
| PostgreSQL | `localhost:${POSTGRES_PORT}` | Base de datos (acceso directo) |

> Los puertos dependen de los valores definidos en tu archivo `.env`.

---

## Equipo

Desarrollado por:

| Integrante | GitHub |
|-----------|--------|
| Salomon Avila | [@SalomonAvila](https://github.com/SalomonAvila) |
| Gabriel Jaramillo | [@GabrielJaramilloCuberos](https://github.com/GabrielJaramilloCuberos) |
| Valeria Herrera | [@valerrera](https://github.com/valerrera) |
| Ana Maria Romero | [@romerocana](https://github.com/romerocana) |

---
