# Prerequisites
- Java 21
- Maven (or use provided `mvnw`)
- Docker & Docker Compose (for containerized setup)

# Setup Guide

## 1. Clone the Project
```bash
git clone <repository-url>
cd "Zero Trust Authentication System"
```

## 2. Environment Configuration
The application relies strictly on environment variables for configuration.
Copy the provided example file to create your local `.env`:
```bash
cp .env.example .env
```
_Note: The `.env` file is git-ignored to prevent secrets from leaking into version control._

## 3. Run Locally with Docker (Recommended)
This will spin up the Application, MySQL, and Redis all configured via your `.env` file.
```bash
docker compose up --build -d
```
Check logs:
```bash
docker compose logs -f app
```

## 4. Run Locally without Docker (Development)
If you prefer running the Java app on your host machine while relying on Docker for DB/Redis:
1. Start infrastructure only:
   ```bash
   docker compose up mysql redis -d
   ```
2. Source your `.env` variables (or use your IDE's environment variables config).
3. Run the app:
   ```bash
   ./mvnw spring-boot:run
   ```

## 5. Running Tests
```bash
./mvnw test
```

## 6. Accessing Swagger API Documentation
When `SWAGGER_ENABLED=true` in your `.env`, access the UI at:
http://localhost:8080/swagger-ui.html

## 7. Importing Postman Collection
1. Open Postman.
2. Click **Import**.
3. Select the `ZeroTrustAuth_Postman_Collection.json` file located in the project root.
4. Set up your environments if needed (e.g., using `APP_BASE_URL`).

## 8. Checking Containers
**MySQL:**
```bash
docker exec -it zerotrust_mysql mysql -u root -p
```
**Redis:**
```bash
docker exec -it zerotrust_redis redis-cli -a <your_redis_password>
```

## 9. Stopping & Resetting
Stop containers:
```bash
docker compose down
```
Reset database (Wipes all volumes!):
```bash
docker compose down -v
```

## 10. Common Errors and Fixes
- **Port already in use:** Change `APP_PORT_MAPPING`, `DB_PORT_MAPPING`, or `REDIS_PORT_MAPPING` in your `.env` file.
- **Database Connection Refused:** Ensure MySQL container is fully healthy before the app starts. The `docker-compose.yml` uses `depends_on: condition: service_healthy` to handle this automatically.
- **Unauthorized (401) on all requests:** Check your `JWT_SECRET` in `.env`. If it changes, all previously issued tokens become invalid.

## 11. Production Deployment Checklist
- [ ] Create a `.env` file on your production server based on `.env.production.example`.
- [ ] Ensure `JWT_SECRET` is strong (256-bit+ random string) and kept secure.
- [ ] Set `DB_PASSWORD`, `REDIS_PASSWORD`, and `MYSQL_ROOT_PASSWORD` to strong values.
- [ ] Change `CORS_ALLOWED_ORIGINS` to your actual frontend domains.
- [ ] Set `SWAGGER_ENABLED=false` to disable public API docs.
- [ ] Set `LOG_LEVEL_ROOT=WARN` and `LOG_LEVEL_APP=INFO`.
