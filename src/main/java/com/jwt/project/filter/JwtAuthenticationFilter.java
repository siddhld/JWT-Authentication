package com.jwt.project.filter;

import com.jwt.project.service.JwtService;
import com.jwt.project.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsimplService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsimplService) {
        this.jwtService = jwtService;
        this.userDetailsimplService = userDetailsimplService;
    }

//    When a client sends a request to a web application, the request passes through a series of filters before reaching the target servlet or resource.
//    Each filter in the chain is responsible for performing specific tasks, such as logging, authentication, data compression,
//    or any other pre-processing or post-processing activities.
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

//        SecurityContextHolder is used to store the security context of the current thread. The security context contains
//        important information about the currently authenticated user, such as their username, roles, and permissions
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsimplService.loadUserByUsername(username);

            if (jwtService.isValid(token, userDetails)) {
//      It's used in web applications to verify a user's identity and grant them access to certain features or
//      resources based on their permissions. We use it to ensure that only authorized users can access specific
//      parts of the application, keeping the application secure.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
