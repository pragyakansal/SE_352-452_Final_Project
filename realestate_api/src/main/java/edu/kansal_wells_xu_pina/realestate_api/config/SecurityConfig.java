package edu.kansal_wells_xu_pina.realestate_api.config;

import edu.kansal_wells_xu_pina.realestate_api.services.CustomUserDetailsService;
import edu.kansal_wells_xu_pina.realestate_api.utils.GlobalRateLimiterFilter;
import edu.kansal_wells_xu_pina.realestate_api.utils.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.Filter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomUserDetailsService userDetailsService;

    private final GlobalRateLimiterFilter globalRateLimiterFilter;

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService,
                          GlobalRateLimiterFilter globalRateLimiterFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.globalRateLimiterFilter = globalRateLimiterFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // First, rate limit EVERY request as early as possible:
                .addFilterBefore(globalRateLimiterFilter, UsernamePasswordAuthenticationFilter.class)
                // Then, process JWT authentication:
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/landing-page", "/landing-page/", "/landing-page/login", "/landing-page/register", "/register", "/login", "/images/**", "/css/**").permitAll()
                        .requestMatchers("/landing-page/dashboard", "/landing-page/profile/**", "/landing-page/logout").hasAnyRole("BUYER", "AGENT", "ADMIN")
                        .requestMatchers("/admin/**", "/admin/create-agent").hasRole("ADMIN")
                        .requestMatchers("/agent/**").hasRole("AGENT")
                        .requestMatchers("/buyer/**").hasRole("BUYER")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())  // Handles 401 errors
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        //return http.build();
        DefaultSecurityFilterChain securityFilterChain = http.build();
        /* List<Filter> filters = securityFilterChain.getFilters();
        for (Filter filter : filters) {
            log.info("filter name: " + filter.toString());
            log.info("filter class: " + filter.getClass().getName());
        } */
        return securityFilterChain;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder encoder) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder);

        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
