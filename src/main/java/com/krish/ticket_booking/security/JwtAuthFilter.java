package com.krish.ticket_booking.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwt) { this.jwtService = jwt; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response,@Nonnull FilterChain filterChain)
            throws ServletException, IOException {

//        if (request.getServletPath().startsWith("/api/auth")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String reqHeader = request.getHeader("Authorization");
        if (reqHeader == null || !reqHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); return;
        }

        String token = reqHeader.substring(7);
        try {
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            var auth = new UsernamePasswordAuthenticationToken(
                    email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ignored) {}

        filterChain.doFilter(request, response);
    }
}
