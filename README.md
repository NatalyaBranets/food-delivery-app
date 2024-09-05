# Food-Delivery-App

### Technologies:
- Java 17
- PostgreSQL Database
- Spring Boot 3.2.8
- Spring Data JPA
- Spring Security
- JSON Web Tokens (JWT)
- Flyway
- Testcontainers
- JUnit 5
- Mockito 
- Postman
- Docker
- Maven


### Run:
1. Create database foodhub_db:
CREATE DATABASE IF NOT EXISTS foodhub_db;

2. Run app command:
   - delivery-api$ mvn spring-boot:run

3. Open Postman and run commands from postman collection:
   - delivery-api/foodhub.postman_collection.json 

   Don't forget to update jwt token in authenticated http requests. You can receive it with:
   - POST http://localhost:9000/foodhub/v1/auth/login
   - POST http://localhost:9000/foodhub/v1/auth/register

   Default user: "username": "admin@gmail.com", "password": "admin"


### Features:
**1. Security:**
   - User registration and login with JWT authentication
   - Password encryption using BCrypt
   - Role-based authorization with Spring Security
   - Customized access denied handling.
     
**2. Global Exception Handler**

**3. Simplified Database Migrations with Flyway**

**4. Unit and Integration tests**


### Database Design for a Food Delivery App:

![delivery_db_schema](https://github.com/user-attachments/assets/77ca2e21-9c52-4d2b-bb7c-3fd1a25c414b)

