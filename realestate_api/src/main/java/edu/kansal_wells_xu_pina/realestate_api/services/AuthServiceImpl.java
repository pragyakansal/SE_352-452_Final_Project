package edu.kansal_wells_xu_pina.realestate_api.services;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.InvalidUserParameterException;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.AlreadyExistsException;
import edu.kansal_wells_xu_pina.realestate_api.dtos.JwtResponse;
import edu.kansal_wells_xu_pina.realestate_api.jwt.JwtUtil;
import edu.kansal_wells_xu_pina.realestate_api.utils.JwtAuthResponse;
import edu.kansal_wells_xu_pina.realestate_api.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private CustomUserDetailsService customUserDetailsService;
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Cookie loginAndCreateJwtCookie(User user) throws BadCredentialsException {
        try {
            log.info("Attempting to authenticate user: {}", user.getEmail());
            
            // Get the user from database first to ensure we have the correct role
            User dbUser = userRepository.findByEmail(user.getEmail());
            if (dbUser == null) {
                log.error("User not found in database: {}", user.getEmail());
                throw new BadCredentialsException("Invalid email or password");
            }
            
            // Authenticate the user
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            log.info("User authenticated successfully: {}", user.getEmail());
            
            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("Security context set for user: {}", user.getEmail());
            
            // Generate JWT token using the database user's role
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(dbUser.getEmail());
            String token = jwtUtil.generateToken(userDetails);
            log.info("JWT token generated for user: {} with role: {}", user.getEmail(), dbUser.getRole());

            // Create and configure the JWT cookie
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60); // 1 hour
            log.info("JWT cookie created for user: {}", user.getEmail());

            return jwtCookie;
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", user.getEmail(), e);
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'BUYER')")
    public void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        log.info("JWT cookie cleared");
    }

    @Override
    public JwtResponse authenticateAndGenerateToken(User user) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            log.info("test1");
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("test2");
            // Get the actual user from the database to ensure we have the correct role
            User dbUser = userRepository.findByEmail(user.getEmail());

            log.info("test3");
            if (dbUser == null) {
                log.info(" dbUser is null");
                throw new BadCredentialsException("User not found");
            } else {
                log.info("dbUser emaiil:" + dbUser.getEmail());
                log.info("dbUser role:" + dbUser.getRole());
            }

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(dbUser.getEmail());
            log.info("userDetails:" + userDetails);
            String token = jwtUtil.generateToken(userDetails);
            log.info("token:" + token);

            return new JwtResponse(token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public User registerUser(User newUser) {
        User existingUser = userRepository.findByEmail(newUser.getEmail());
        if (existingUser != null) {
            throw new AlreadyExistsException("A user with the email: " + newUser.getEmail() + " already exists. Please login " +
                    " to your existing account or create an account with a new email.");
        }
        validateUserRegistrationFields(newUser);

        // Set role to BUYER for new registrations
        newUser.setRole(Role.BUYER);
        // Set creation timestamp
        newUser.setCreatedAt(LocalDateTime.now());
        
        // Encrypt password
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        
        return userRepository.save(newUser);
    }

    public JwtAuthResponse loginUser(User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser == null) {
            throw new InvalidUserParameterException("A user with the email: " + user.getEmail() + " could not be found in the database. " +
                    " Please try again and login with an email that is already registered within our system.");
        }

        // NEW CHANGES
        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            throw new InvalidUserParameterException("The password entered is incorrect. Please try again with the correct password.");
        }
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        return new JwtAuthResponse(jwt);
    }

    private void validateUserRegistrationFields(User newUser) {
        if (newUser.getFirstName() == null || newUser.getFirstName().trim().isEmpty()) {
            throw new InvalidUserParameterException("The user's first name should not be null or empty. Please try again with a valid input.");
        }

        if (newUser.getLastName() == null || newUser.getLastName().trim().isEmpty()) {
            throw new InvalidUserParameterException("The user's last name should not be null or empty. Please try again with a valid input.");
        }

        if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty()) {
            throw new InvalidUserParameterException("The user's email must contain an @ symbol, and it should not be null or empty. " +
                    " Please try again with a valid input.");
        }

        // Checking correct format for email
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!newUser.getEmail().matches(emailRegex)) {
            throw new InvalidUserParameterException("The email format is incorrect. Please try again with a valid input.");
        }

        if (newUser.getPassword() == null || newUser.getPassword().length() < 7 || newUser.getPassword().length() > 14 || !newUser.getPassword().matches(".*[^a-zA-Z0-9].*")) {
            throw new InvalidUserParameterException("The user's password must contain at least 7 characters and at most 14 characters, " +
                    " and it must have at least one special character. Please try again with a valid input.");
        }
    }
}
