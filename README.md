# Food-Delivery-App

## Technologies:
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
- GitHub Actions 
- Postman
- Docker
- Maven


## Run:

```shell
$ git clone https://github.com/NatalyaBranets/food-delivery-app.git
$ cd food-delivery-app
$ chmod +x run.sh
$ ./run.sh start
$ ./run.sh stop
```

Open Postman and run http requests from 'delivery-api/foodhub.postman_collection.json'. 
Don't forget to update jwt token in authenticated http requests. You can receive it with:
   - POST http://localhost:9000/foodhub/v1/auth/login
   - POST http://localhost:9000/foodhub/v1/auth/register

Default user: "username": "admin@gmail.com", "password": "admin"


## Features:
1. Security:
   - User registration and login with JWT authentication
   - Password encryption using BCrypt
   - Role-based authorization with Spring Security
   - Customized access denied handling.
   
2. Global Exception Handler

3. Simplified Database Migrations with Flyway

4. Unit and Integration tests

