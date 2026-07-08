# ZeroTrustAuth System

A production-grade, enterprise-level authentication and authorization system built with **Java 21**, **Spring Boot 3**, **Spring Security 6**, **MySQL**, and **Redis**.

## Zero Trust Philosophy: "Never Trust, Always Verify"
This project adheres to Zero Trust principles:
1. **Stateless Authentication**: Every request is independently verified using JWT access tokens.
2. **Explicit Trust Boundaries**: Role-Based Access Control (RBAC) and Permission-Based Access Control are enforced on all protected APIs.
3. **Session Revocation**: Tokens can be blacklisted in Redis upon logout, instantly revoking access.
4. **Token Rotation**: Refresh tokens rotate upon every use, minimizing the blast radius of a leaked token.
5. **Auditing**: Comprehensive audit logs track every sensitive action (login, logout, refresh, etc.).

## Architecture
```text
Client Request
      │
      ▼
[API Gateway/Load Balancer] (Simulated/Future)
      │
      ▼
[Spring Security Filter Chain]
  ├── JwtAuthenticationFilter (Extracts token, validates signature)
  ├── Redis Check (Verifies token is not blacklisted)
  └── Authorization Check (Validates Roles & Permissions)
      │
      ▼
[Controllers (Auth, User, Admin)]
      │
      ▼
[Services] ────▶ [Redis (Token Blacklist, Rate Limiting)]
      │
      ▼
[Repositories] ────▶ [MySQL (Users, Roles, Tokens, Logs)]
```

## Features
- **User Registration & Login** (BCrypt hashing)
- **JWT Access & Refresh Tokens**
- **Refresh Token Rotation** (Old token invalidated, new one issued)
- **Logout & Logout-All** (Redis Blacklisting)
- **RBAC & PBAC** (Admin vs. User, fine-grained permissions)
- **Audit Logging** (Tracking IP, User-Agent, Actions)
- **Account Locking** (Ready for implementation via failed attempts)

## Environment Configuration
This project uses `.env` files for all configuration, separating local variables from production secrets.

### Local vs Production Config
- **Local:** Use `.env.example` as a template for your local `.env`.
- **Production:** Use `.env.production.example` as a reference for your production environment.

### Security Notes
- The `.env` file is excluded from version control (`.gitignore`).
- Ensure `JWT_SECRET` is set to a secure, 256-bit random string in production.
- Do not use `localhost` or `*` for `CORS_ALLOWED_ORIGINS` in production.
- Disable Swagger in production via `SWAGGER_ENABLED=false`.

### Deployment Notes
- All infrastructure relies on Docker Compose with named volumes. Ensure proper backup mechanisms are configured for `mysql_data`.
- For detailed deployment steps, please read the Setup Guide below.

## Setup Guide
Please refer to the [SETUP.md](SETUP.md) for detailed instructions on prerequisites, running locally, and production deployment checklists.

## Prerequisites
- Docker & Docker Compose installed.

## Setup Instructions
Please refer to [SETUP.md](SETUP.md) for full instructions on creating your `.env` file and running the containers.

```bash
cp .env.example .env
docker compose up --build -d
```

## Postman Testing Guide
1. Import the included `ZeroTrustAuth_Postman_Collection.json` into Postman.
2. Execute the `Register` request to create a user.
3. Execute the `Login` request. The Access Token and Refresh Token will be returned.
4. Add the `accessToken` to the Authorization header (Bearer token) for protected APIs.
5. Test the `Get Profile`, `Admin Users` (requires Admin role), and `Refresh Token` flows.



## Future Scope
- Implement MFA (Multi-Factor Authentication).
- Integrate OAuth2 (Google/GitHub login).
- Advanced Rate Limiting via Bucket4j.
