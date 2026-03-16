package com.br.barbershop.managerbarbershop.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHelper helper;

    public JwtAuthenticationFilter(JwtHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String rawToken = recoverToken(request);

        if (rawToken != null) {
            String subject = helper.validateToken(rawToken);

            if (!subject.isEmpty()) {
                String role   = helper.extractRole(rawToken);
                Integer userId = helper.extractUserId(rawToken);

                // Build authentication from JWT claims — no DB lookup per request (stateless).
                // Spring Security checks ROLE_{role} against SecurityConfig matchers.
                var authorities = AuthorityUtils.createAuthorityList("ROLE_" + role);
                var auth = new UsernamePasswordAuthenticationToken(subject, null, authorities);

                // userId is stored in details so controllers can retrieve it via
                // (Integer) authentication.getDetails()
                auth.setDetails(userId);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }
}
