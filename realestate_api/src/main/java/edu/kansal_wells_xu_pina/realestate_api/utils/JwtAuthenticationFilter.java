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
import jakarta.servlet.http.HttpSession;

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

        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        String path = request.getRequestURI();

        if (path.startsWith("/landing-page/login") || path.startsWith("/css") || path.startsWith("/landing-page/register") || path.startsWith("/images")) {
            filterChain.doFilter(request, response);
            return;
        }

        /* if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                log.error("JWT parsing failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token");
                return;
            }
            if (username == null) {
                log.warn("JWT provided but username could not be extracted.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token - Missing username");
                return;
            }
        } */

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        } else {
            // 2. If not in header, try session
            HttpSession session = request.getSession(false);
            if (session != null) {
                token = (String) session.getAttribute("jwt");
            }

            // 3. Optionally, try cookies (if you stored JWT there)
            if (token == null && request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    log.info("Found cookie: {}={}", cookie.getName(), cookie.getValue());
                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        log.info("JWT found in cookie: {}", token);
                        break;
                    }
                }
            }
        }

        if (token != null) {
            log.info("JWT token found: {}", token);
            try {
                username = jwtUtil.extractUsername(token);
                log.info("Extracted username from token: {}", username);
            } catch (Exception e) {
                log.error("JWT parsing failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token");
                return;
            }

            if (username == null) {
                log.warn("Token provided but username could not be extracted.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token - Missing username");
                return;
            }
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                List<String> roles = jwtUtil.extractRoles(token);

                var authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Authentication successfully set for user: {}", username);
            }
        }

        log.info("Current authentication context: {}", SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }
}
