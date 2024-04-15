package com.jwt.project.service;

import com.jwt.project.dto.AuthenticationResponse;
import com.jwt.project.exception.CustomAccessDeniedHandler;
import com.jwt.project.exception.UserAlreadyLoggedInException;
import com.jwt.project.model.Token;
import com.jwt.project.model.User;
import com.jwt.project.repository.TokenRepository;
import com.jwt.project.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;


@Log4j2
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public AuthenticationService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
     TokenRepository tokenRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
         this.tokenRepository = tokenRepository;
    }

    public AuthenticationResponse register(User request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        String jwtToken = jwtService.generateToken(user);

        // user.setTokens(List.of(tokenObj));
        user = userRepository.save(user);

        // Generating and setting Token's data
        saveTokenObj(user, jwtToken);

        return new AuthenticationResponse(jwtToken, "Token generated successfully");
    }

     private void saveTokenObj(User user, String jwtToken) {
     Token tokenObj = new Token();
     tokenObj.setToken(jwtToken);
     tokenObj.setUser(user);
     tokenObj.setLoggedOut(false);
     tokenRepository.save(tokenObj);
     }

    public AuthenticationResponse authenticate(User request) throws UserAlreadyLoggedInException {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        try {
            authenticationManager.authenticate(
//  It's used in web applications to verify a user's identity and grant them access to certain features or
//  resources based on their permissions. We use it to ensure that only authorized users can access specific
//  parts of the application, keeping the application secure.
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            revokeAllTokensByUser(user);
            List<Token> tt = tokenRepository.findAll();
            saveTokenObj(user, token);
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return new AuthenticationResponse(token, "User logged successfully");
    }

    private void revokeAllTokensByUser(User request) {
//      When a user is trying to "login" all the previous tokens related to the current user's should have to be invalid
//      and the new token will be valid that is added in "line no. 55"
        List<Token> tokens = tokenRepository.findAllTokenByUser(request.getId());
        if(!tokens.isEmpty()){
            tokens.forEach(t -> t.setLoggedOut(true));
        }
        tokenRepository.saveAll(tokens);
    }
}
