package com.team1.paymentsystem.auth;

import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.services.entities.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

@Log
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    ApplicationContext context;
    @Autowired
    private UserInfoUserDetailsService userInfoUserDetailsService;
    @Autowired
    private JwtService jwtService;

    /*@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Entered authentication filter");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if(isAuthenticated(req) || req.getRequestURL().toString().endsWith("login")||
        req.getMethod().equals("OPTIONS") || req.getRequestURL().toString().endsWith("register")) {
            chain.doFilter(req, res);
        }
        else {
            log.severe("unauthorized access " + req.getRequestURL().toString() + " " + ((HttpServletRequest) request).getMethod());
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        log.info("Exiting authentication filter");
    }*/

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Entered authentication filter");
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
                UserDetails userDetails = userInfoUserDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.severe("authenticated user " + username + ", setting security context" + authToken);
                }
            }
            filterChain.doFilter(req, res);
        }
        else {
            log.severe("unauthorized access " + req.getRequestURL().toString() + " " + (request).getMethod());
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        log.info("Exiting authentication filter");
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        String username = request.getHeader("username");
        String profileName = request.getHeader("profileName");
        log.info("username: " + username + " profile: "
                + profileName + " " + request.getRequestURL().toString() + " " + request.getMethod());
        UserService userService = context.getBean(UserService.class);
        User user = userService.findByUsername(username); // removed profile name
        return user != null;
    }
}






@Log
class Logging{
    public static void logRequestHeaders(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n Request headers for ").append(request.getRequestURI()).append(" with method: ").append(request.getMethod()).append(":{\r\n");
        Enumeration<String> en = request.getHeaderNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            sb.append("Request Header: " + name + " = " + request.getHeader(name) + "\r\n");
        }
        sb.append("}");
        log.info(String.valueOf(sb));
    }

    public static void logSessionAttributes(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n Session attributes for ").append(request.getSession().hashCode()).append(":{\r\n");
        Enumeration<String> names = request.getSession().getAttributeNames();
        while (names.hasMoreElements()) {
            String att = names.nextElement();
            sb.append("Session attribute: " + att + " = " + request.getSession().getAttribute(att) + "\r\n");
        }
        sb.append("}");
        log.info(String.valueOf(sb));
    }

    public static void logRequestParameters(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n Request parameters for  ").append(request.getRequestURI()).append(" with method: ").append(request.getMethod()).append(":{\r\n");
        Map<String, String[]> s = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : s.entrySet()) {
            sb.append("Request parameter : " + entry.getKey() + "=" + Arrays.toString(entry.getValue()) + "\r\n");
        }
        sb.append("}");
        log.info(String.valueOf(sb));
    }
    public static void logRequestAttributes(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n Request attributes for  ").append(request.getRequestURI()).append(" with method: ").append(request.getMethod()).append(":{\r\n");
        Enumeration<String> names = request.getAttributeNames();
        while (names.hasMoreElements()) {
            String att = names.nextElement();
            sb.append("Request attribute: " + att + " = " + request.getAttribute(att) + "\r\n");
        }
        sb.append("}");
        log.info(String.valueOf(sb));
    }

}
