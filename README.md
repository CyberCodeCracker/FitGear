# FitGear

FitGear is a full-stack online fitness coaching platform. It connects coaches and clients so coaches can manage training and nutrition plans, while clients can discover coaches, subscribe to a coach, follow assigned programs, and track their progress.

## What This Website Does

FitGear supports two main user roles:

- **Clients** can register, browse available coaches, view coach profiles and testimonials, subscribe to a coach, view their assigned training and nutrition programs, update their profile, upload a profile picture, and record progress entries such as weight, body fat, muscle mass, and notes.
- **Coaches** can register, manage their public coaching profile, view assigned clients, inspect client details, create or replace client training programs, create or replace client diet programs, and use the exercise catalog when building workouts.

The platform includes the core business flow for remote coaching:

1. A coach creates a profile with experience, monthly rate, description, phone number, availability, and optional profile image.
2. A client registers and browses coaches.
3. The client subscribes to one coach.
4. The coach manages that client's training and nutrition plans.
5. The client follows the assigned plans and tracks progress over time.
6. Clients can leave testimonials for coaches.

On startup, the API seeds demo roles, coaches, clients, and an exercise catalog. Seeded users use the password `Password1!`; examples include `coach@fitgear.com` and `client@fitgear.com`.

## Technologies Used

### Frontend

- **Angular 19** for the single-page application.
- **TypeScript** for typed frontend code.
- **Angular Router** for public, client, coach, and profile routes.
- **Angular HttpClient** for API communication.
- **Angular signals** for auth/user state in the frontend.
- **Tailwind CSS** with PostCSS and Autoprefixer for styling.
- **RxJS** for asynchronous API flows.
- **Karma/Jasmine** for Angular unit testing.

### Backend

- **Java 17**.
- **Spring Boot 3.4.2**.
- **Spring Web** for REST APIs.
- **Spring Security** with JWT authentication.
- **Spring Data JPA / Hibernate** for persistence.
- **PostgreSQL** as the database.
- **Spring Mail** with **MailDev** for local email testing.
- **Thymeleaf** for the account activation email template.
- **Springdoc OpenAPI** for API documentation.
- **Lombok** for reducing Java boilerplate.
- **Maven Wrapper** for backend builds and local runs.

### Local Infrastructure

- **Docker Compose** starts PostgreSQL and MailDev.
- PostgreSQL runs on host port `5433`.
- MailDev SMTP runs on `1025`, and the MailDev web UI runs on `1080`.
- The backend serves APIs under `http://localhost:8080/api/v1`.
- The Angular app runs at `http://localhost:4200`.

## Project Structure

```text
FitGear/
  fitgear-api/          Spring Boot backend
  fitgear-frontend/     Angular frontend
  uploads/              Local uploaded files, such as profile pictures
  docker-compose.yml    PostgreSQL and MailDev services
```

## Prerequisites

Install these before running the project locally:

- Java 17
- Node.js and npm
- Docker Desktop, or another Docker Compose-compatible runtime

You do not need to install Maven globally because the backend includes Maven Wrapper scripts.

## Running Locally

### 1. Start the Local Services

From the repository root:

```bash
docker compose up -d
```

This starts:

- PostgreSQL at `localhost:5433`
- MailDev SMTP at `localhost:1025`
- MailDev web inbox at `http://localhost:1080`

### 2. Start the Backend API

In a second terminal:

```bash
cd fitgear-api
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd fitgear-api
.\mvnw.cmd spring-boot:run
```

The API uses the `dev` profile by default. It connects to:

```text
jdbc:postgresql://localhost:5433/fit_gear
username: post
password: post
```

The backend will be available at:

```text
http://localhost:8080/api/v1
```

### 3. Start the Frontend

In a third terminal:

```bash
cd fitgear-frontend
npm install
npm start
```

Open:

```text
http://localhost:4200
```

## Demo Accounts

After the backend starts, the data seeder creates demo users. Use:

```text
Password: Password1!
```

Example accounts:

- Coach: `coach@fitgear.com`
- Client: `client@fitgear.com`

Additional seeded coaches and clients are created for browsing and testing.

## Useful Local URLs

- Frontend: `http://localhost:4200`
- Backend API base URL: `http://localhost:8080/api/v1`
- MailDev inbox: `http://localhost:1080`
- OpenAPI/Swagger UI: `http://localhost:8080/api/v1/swagger-ui/index.html`

## Common Commands

Frontend:

```bash
cd fitgear-frontend
npm start
npm run build
npm test
```

Backend:

```bash
cd fitgear-api
./mvnw spring-boot:run
./mvnw test
```

Windows PowerShell backend commands:

```powershell
cd fitgear-api
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
```

## Notes

- The frontend currently points directly to `http://localhost:8080/api/v1` in its Angular services.
- Uploaded profile pictures are stored locally under `uploads/`.
- Database schema updates are handled by Hibernate with `ddl-auto: update` in the development profile.
- Registration emails are sent to MailDev during local development.
