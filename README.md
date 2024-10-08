# Food-Delivery-App
REST API Online food delivery application allows users to order food and rate each restaurant.

## Features:
1. **User Security:** Implemented JWT (JSON Web Tokens) for secure user authentication and authorization. Users can register, log in, and access their account. Passwords are encrypted using BCrypt.
2. **Global Exception Handler:** Created a centralized exception handler that can handle exceptions thrown by controllers in my application. Used @ControllerAdvice and @ExceptionHandler. 
3. **DB Migration:** Used Flyway tool that automates the execution of migration scripts by eliminating the need for manual intervention and helping in precise version control. To reduce complexity, I placed migration scripts into separate folders based on their purpose.
4. **Email Verification:** User should verify his email address after registration. A verification link is sent to his email for confirmation and is valid within 3 minutes.
5. **Tests:** Implemented Unit and Integration tests for controller, service and repository levels.

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

Default user: "username": "admin@gmail.com", "password": "admin"




