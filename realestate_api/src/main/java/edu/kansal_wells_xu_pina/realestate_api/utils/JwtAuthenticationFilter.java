package edu.kansal_wells_xu_pina.realestate_api.utils;
import edu.kansal_wells_xu_pina.realestate_api.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter invoked for URI: {}", request.getRequestURI());

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        log.info("Normalized path: {}", path);

        // Skip JWT processing for public endpoints
        if (path.startsWith("/landing-page/login") || 
            path.startsWith("/landing-page/register") || 
            path.equals("/landing-page/") ||  // Only the landing page itself
            path.startsWith("/css") || 
            path.startsWith("/images") ||
            path.equals("/") ||
            path.equals("/register") ||
            path.equals("/login")) {
            log.debug("Skipping JWT processing for public endpoint: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;

        // Try to get token from cookie
        if (request.getCookies() != null) {
            log.debug("Found {} cookies in request", request.getCookies().length);
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    log.info("JWT found in cookie");
                    break;
                }
            }
        } else {
            log.debug("No cookies found in request");
        }

        if (token == null) {
            log.warn("No JWT token found in request cookies for URI: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = jwtUtil.extractUsername(token);
            log.info("Extracted email from token: {}", email);

            if (email != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.info("Loaded user details for: {}", email);

                if (jwtUtil.validateToken(token, userDetails)) {
                    List<String> roles = jwtUtil.extractRoles(token);
                    log.info("Extracted roles from token: {}", roles);

                    var authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                    log.info("Created authorities: {}", authorities);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authentication successfully set for user: {}", email);
                } else {
                    log.warn("Token validation failed for user: {}", email);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
        }

        filterChain.doFilter(request, response);
    }
}