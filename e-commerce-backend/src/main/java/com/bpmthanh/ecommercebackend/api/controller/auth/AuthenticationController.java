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
import com.bpmthanh.ecommercebackend.exception.EmailFailureException;
import com.bpmthanh.ecommercebackend.exception.UserNotVerifiedException;
import org.springframework.web.bind.annotation.RequestParam;

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
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
        String message;
        int status;
        String jwt = null;

        try {
            jwt = userService.loginUser(loginBody);
        } catch (UserNotVerifiedException ex) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if (ex.isNewEmailSent()) {
                reason += "_EMAIL_RESENT";
            }
            response.setFailureReason(reason);
            CustomResponse<LoginResponse> customResponse = new CustomResponse<>();
            customResponse.setStatus(HttpStatus.FORBIDDEN.value());
            customResponse.setResponseBody(response);
            customResponse.setMessage("Login failed!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(customResponse);
        } catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

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

    /**
     * Post mapping to verify the email of an account using the emailed token.
     * 
     * @param token The token emailed for verification. This is not the same as a
     *              authentication JWT.
     * @return 200 if successful. 409 if failure.
     */
    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token) {
        if (userService.verifyUser(token)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}