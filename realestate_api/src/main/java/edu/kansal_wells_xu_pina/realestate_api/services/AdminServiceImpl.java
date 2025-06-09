package edu.kansal_wells_xu_pina.realestate_api.services;
import edu.kansal_wells_xu_pina.realestate_api.enums.Role;
import edu.kansal_wells_xu_pina.realestate_api.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import java.util.List;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.InvalidUserParameterException;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.AlreadyExistsException;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AdminServiceImpl implements AdminService {

    private UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        if (allUsers.isEmpty()) {
            throw new NotFoundException("No users could be found in the database.");
        }
        return allUsers;
    }

    @Override
    @Transactional
    public String deleteUserByEmailAddress(String email) {
        User userToDelete = userRepository.findByEmail(email);
        if (userToDelete == null) {
            throw new NotFoundException("The user with the email address: " + email + " could not be found. " +
                    "Please try again by deleting a user that already exists in the database.");
        }
        userRepository.delete(userToDelete);
        return "The user with the email address: " + userToDelete.getEmail() + " was deleted successfully.";
    }

    @Override
    @Transactional
    public User createAgentUser(User newAgentUser) {
        validateAgentFields(newAgentUser);
        User existingUserAgent = userRepository.findByEmail(newAgentUser.getEmail());
        if (existingUserAgent != null) {
            throw new AlreadyExistsException("The agent with the email address: " + newAgentUser.getEmail() + " already exists in the database. " +
                    " Please try again by creating a new agent with a unique email address.");
        }
        String rawPassword = newAgentUser.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        log.info("Raw password: {}", newAgentUser.getPassword());
        newAgentUser.setPassword(passwordEncoder.encode(rawPassword));
        log.info("Encoded password: {}", newAgentUser.getPassword());
        User user = null;
        try {
            if (newAgentUser.getRole() == null || newAgentUser.getRole().equals("")) {
                log.info("User role is null, setting to AGENT");
                newAgentUser.setRole(Role.AGENT);
            }
            newAgentUser.setCreatedAt(LocalDateTime.now());
            log.info("User role before saving: " + newAgentUser.getRole());
            user = userRepository.save(newAgentUser);
        } catch (Exception e) {
            log.error("Error in saving user: " + newAgentUser.getEmail() + ", role: " + newAgentUser.getRole(),  e);
        }
        return user;
    }

    private void validateAgentFields(User agentUser) {
        if (agentUser.getFirstName() == null || agentUser.getFirstName().trim().isEmpty()) {
            throw new InvalidUserParameterException("The agent's first name should not be null or empty. Please try again with a valid input.");
        }

        if (agentUser.getLastName() == null || agentUser.getLastName().trim().isEmpty()) {
            throw new InvalidUserParameterException("The agent's last name should not be null or empty. Please try again with a valid input.");
        }

        if (agentUser.getEmail() == null || agentUser.getEmail().trim().isEmpty()) {
            throw new InvalidUserParameterException("The agent's email must contain an @ symbol, and it should not be null or empty. " +
                    " Please try again with a valid input.");
        }

        // Correct format for email
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!agentUser.getEmail().matches(emailRegex)) {
            throw new InvalidUserParameterException("The email format is incorrect. Please try again with a valid input.");
        }

        if (agentUser.getPassword() == null || agentUser.getPassword().length() < 7 || agentUser.getPassword().length() > 14 || !agentUser.getPassword().matches(".*[^a-zA-Z0-9].*")) {
            throw new InvalidUserParameterException("The agent's password must contain at least 7 characters and at most 14 characters, " +
                    " and it must have at least one special character. Please try again with a valid input.");
        }
    }
}
