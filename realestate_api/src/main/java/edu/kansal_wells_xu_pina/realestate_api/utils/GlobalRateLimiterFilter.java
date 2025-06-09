package edu.kansal_wells_xu_pina.realestate_api.utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GlobalRateLimiterFilter extends OncePerRequestFilter {
    private static final int MAX_REQUESTS = 100; // per time window
    private static final long WINDOW_MS = 60_000; // 1 minute

    private static final Logger log = LoggerFactory.getLogger(GlobalRateLimiterFilter.class);
    private final Map<String, RequestWindow> ipRequests = new ConcurrentHashMap<>();

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        Instant now = Instant.now();

        RequestWindow window = ipRequests.computeIfAbsent(ip, k -> new RequestWindow());

        log.info("Beginning of global rate limiter filter");

        synchronized (window) {
            if (now.isAfter(window.windowStart.plusMillis(WINDOW_MS))) {
                window.windowStart = now;
                window.requestCount = 1;
            } else {
                window.requestCount++;
            }

            if (window.requestCount > MAX_REQUESTS) {
                response.setStatus(429);
                response.setContentType("application/json");
                String s = String.format(
                        "{\n" +
                                "  \"error\": \"Rate Limit Exceeded\",\n" +
                                "  \"message\": \"Exceeded %d calls per %d seconds. Please slow down.\"\n" +
                                "}",
                        MAX_REQUESTS, WINDOW_MS / 1000
                );

                response.getWriter().write(s);
                return;
            }
        }
        log.info("End of global rate limiter filter");
        filterChain.doFilter(request, response);
    }

    private static class RequestWindow {
        Instant windowStart = Instant.now();
        int requestCount = 0;
    }
}
