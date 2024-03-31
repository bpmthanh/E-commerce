package com.bpmthanh.ecommercebackend.api.controller.auth;

import com.bpmthanh.ecommercebackend.api.model.RegistrationBody;
import com.bpmthanh.ecommercebackend.exception.UserAlreadyExistsException;
import com.bpmthanh.ecommercebackend.model.LocalUser;
import com.bpmthanh.ecommercebackend.responseEntity.CustomResponse;
import com.bpmthanh.ecommercebackend.service.UserService;
import com.bpmthanh.ecommercebackend.api.model.LoginBody;
import com.bpmthanh.ecommercebackend.api.model.LoginResponse;
import jakarta.validation.Valid;

import org.hibernate.mapping.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for handling authentication requests.
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    /** The user service. */
    private UserService userService;

    /**
     * Spring injected constructor.
     * 
     * @param userService
     */
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Post Mapping to handle registering users.
     * 
     * @param registrationBody The registration information.
     * @return Response to front end.
     */
    @PostMapping("/register")
    public ResponseEntity<CustomResponse<RegistrationBody>> registerUser(
            @Valid @RequestBody RegistrationBody registrationBody) {
        try {
            LocalUser registeredUser = userService.registerUser(registrationBody);
            String message;
            int status;

            if (registeredUser != null) {
                message = "Register successfully!";
                status = 200;
            } else {
                message = "Registration failed!";
                status = 400;
            }

            CustomResponse<RegistrationBody> customResponse = new CustomResponse<>(status, registrationBody, message);
            return ResponseEntity.ok(customResponse);
        } catch (UserAlreadyExistsException ex) {
            String message = "User already exists!";
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new CustomResponse<>(HttpStatus.CONFLICT.value(), registrationBody, message));
        }
    }

    /**
     * Post Mapping to handle user logins to provide authentication token.
     * 
     * @param loginBody The login information.
     * @return The authentication token if successful.
     */
    @PostMapping("/login")
    public ResponseEntity<CustomResponse<LoginResponse>> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = userService.loginUser(loginBody);
        String message;
        int status;

        if (jwt == null) {
            message = "Login failed!";
            status = 400;
            CustomResponse<LoginResponse> customResponse = new CustomResponse<>(status, null, message);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(customResponse);
        } else {
            message = "Login successfully!";
            status = 200;
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            CustomResponse<LoginResponse> customResponse = new CustomResponse<>(status, response, message);
            return ResponseEntity.ok(customResponse);
        }
    }

    /**
     * Gets the profile of the currently logged-in user and returns it.
     * 
     * @param user The authentication principal object.
     * @return The user profile.
     */
    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
        return user;
    }
}