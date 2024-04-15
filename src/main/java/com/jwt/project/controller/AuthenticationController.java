package com.jwt.project.controller;

import com.jwt.project.dto.AuthenticationResponse;
import com.jwt.project.exception.UserAlreadyLoggedInException;
import com.jwt.project.model.User;
import com.jwt.project.service.AuthenticationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User request) {
        log.info("inside Register method");
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request) throws UserAlreadyLoggedInException {
        log.info("inside Login method");
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
