package com.team1.paymentsystem.auth;

import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.services.entities.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    ApplicationContext context;
    @Autowired
    private SpringUserService springUserService;
    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!request.getRequestURL().toString().contains("secure")) log.info("Entered authentication filter");

        HttpServletRequest req = request;
        HttpServletResponse res = response;
        if(isAuthenticated(req) || req.getRequestURL().toString().endsWith("login")||
                req.getMethod().equals("OPTIONS")) {
            //String username = request.getHeader("username");
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                try {
                    username = jwtService.extractUsername(token);
                }
                catch (ExpiredJwtException e){
                    log.severe("Token expired");
                }
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = springUserService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    if(!request.getRequestURL().toString().contains("secure"))  log.info("authenticated user " + username + ", setting security context" + authToken);
                }
            }
            filterChain.doFilter(req, res);
        }
        else {
            log.severe("unauthorized access " + req.getRequestURL().toString() + " " + (request).getMethod());
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        if(!request.getRequestURL().toString().contains("secure"))  log.info("Exiting authentication filter");
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        String username = request.getHeader("username");
        String profileName = request.getHeader("profileName");
        if(!request.getRequestURL().toString().contains("secure"))
            log.info("username: " + username + " profile: "
                + profileName + " " + request.getRequestURL().toString() + " " + request.getMethod());
        UserService userService = context.getBean(UserService.class);
        User user = userService.findByUsername(username); // removed profile name
        return user != null;
    }
}







