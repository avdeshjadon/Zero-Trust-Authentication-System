# Zero Trust Authentication System - Technology Stack

This document outlines the core technologies, frameworks, and cloud services utilized in the development and deployment of the Zero Trust Authentication System, along with their specific roles.

## Core Backend Framework

*   **Java 21**: The primary programming language used for backend development.
*   **Spring Boot (3.3.2)**: The core framework providing the foundation for the application, REST APIs, and embedded server (Tomcat).
*   **Spring Security**: Handles all security aspects, implementing the Zero Trust principles, stateless sessions, and protecting API endpoints.
*   **Spring Data JPA (Hibernate)**: The Object-Relational Mapping (ORM) layer used to interact with the database, map Java objects to tables, and auto-generate the schema.

## Cloud Services & Databases

*   **TiDB Cloud (Serverless)**: A distributed SQL database acting as the primary, permanent data store for user profiles, roles, permissions, and audit logs.
*   **Upstash (Redis)**: A serverless, high-speed, in-memory data structure store used for security measures such as JWT token blacklisting and managing rate-limit counters.
*   **Render**: The cloud Platform-as-a-Service (PaaS) utilized to build the Docker image and host the Spring Boot backend application in a production environment.
*   **Vercel**: The cloud platform used to host the frontend demonstration files (HTML/JS/CSS) which communicate with the backend.

## Security & Authentication

*   **JSON Web Tokens (JWT)**: Used for secure, stateless authentication. The system issues short-lived Access Tokens and long-lived Refresh Tokens.
*   **Bucket4j**: A Java rate-limiting library integrated with Redis to protect the application against brute-force attacks and abuse.
*   **BCrypt**: A password-hashing function used to securely encrypt user passwords before storing them in the database.

## API Documentation & Containerization

*   **Swagger UI (Springdoc OpenAPI)**: Automatically generates comprehensive, interactive API documentation that allows developers to test endpoints (like login, register) directly from the browser.
*   **Docker**: Used to containerize the application, ensuring consistency across local and production environments, and utilized by Render for the build process.
*   **Maven**: The build automation and dependency management tool used to compile the Java project and manage external libraries.
