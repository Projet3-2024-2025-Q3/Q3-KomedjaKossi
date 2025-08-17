package com.example.jobappbackend.config;

import com.example.jobappbackend.service.JwtService;
import com.example.jobappbackend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter that intercepts each HTTP request to validate the presence
 * and validity of a JWT token. If the token is valid, it sets the
 * authentication context for the current user.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userEntityOpt = userService.findByUsername(username);

            if (userEntityOpt.isPresent() && jwtService.isTokenValid(jwt, username)) {
                var userEntity = userEntityOpt.get();

                var authorities = List.of(new SimpleGrantedAuthority(userEntity.getRole()));

                if (userEntityOpt.isPresent() && jwtService.isTokenValid(jwt, username)) {
                    var userEntityopt = userEntityOpt.get();
                    var Authorities = List.of(new SimpleGrantedAuthority(userEntityopt.getRole()));


                    var authToken = new UsernamePasswordAuthenticationToken(
                            userEntityopt.getUsername(),
                            null,
                            Authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
