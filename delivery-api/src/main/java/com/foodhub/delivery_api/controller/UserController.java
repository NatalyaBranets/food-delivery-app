package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.dto.user.RegisterUserRequestDTO;
import com.foodhub.delivery_api.dto.email.MailInfoDTO;
import com.foodhub.delivery_api.dto.user.UpdateUserRequestDTO;
import com.foodhub.delivery_api.dto.user.UserDTO;
import com.foodhub.delivery_api.dto.user.UsersDataDTO;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.service.EmailSenderService;
import com.foodhub.delivery_api.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.foodhub.delivery_api.constants.EmailConstants.EMAIL_ACCOUNT_VERIFICATION_SUBJECT;

@Slf4j
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private UserService userService;
    @Autowired
    private EmailSenderService emailSenderService;

    /**
     * Register user and send verification email
     * @param request - info for new user
     * @return create user dto and HTTP status code 201 created
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid RegisterUserRequestDTO request) {
        UserDTO savedUser = this.userService.register(request);

        // send email for verification
        String mailBody = this.emailSenderService.createVerificationMailBody(savedUser);
        MailInfoDTO info = new MailInfoDTO(savedUser.email(), EMAIL_ACCOUNT_VERIFICATION_SUBJECT, mailBody);

        CompletableFuture.runAsync(() -> this.emailSenderService.sendEmail(info));

        this.executorService.schedule(()-> {
            // If user doesn't confirm his email, he will be deleted from db in 3 minutes
            UserDTO currentUser = this.userService.getUserById(savedUser.id());
            if (!currentUser.isEnabled()) {
                this.userService.deleteNotVerifiedUser(savedUser.id());
                log.info("User {} has been deleted for not verify his account within 3 minutes", currentUser.id());
            }
        }, 3, TimeUnit.MINUTES);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new UserDTO(authenticatedUser));
    }

    @GetMapping
    public ResponseEntity<UsersDataDTO> getAllUsers(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                    @RequestParam(name = "query", defaultValue = "") String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(this.userService.getAllUsers(page));
        }
        return ResponseEntity.ok(this.userService.searchUsers(query, page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        UserDTO user = this.userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long id,
                                              @RequestBody @Valid UpdateUserRequestDTO request) {
        UserDTO updatedUser = this.userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable("id") Long id) {
        UserDTO userDTO = this.userService.deactivateUser(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/verify")
    public ResponseEntity<HttpStatus> confirmEmail(@RequestParam("code") String verificationCode) {
        this.userService.verifyUser(verificationCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
