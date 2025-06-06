package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.dtos.UpdateProfileRequest;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
import edu.kansal_wells_xu_pina.realestate_api.utils.CurrentUserContext;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.InvalidUserParameterException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private CurrentUserContext getCurrentUserContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByEmail(username);
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CurrentUserContext(user, auth);
    }

    @Override
    public void prepareDashboardModel(Model model) {
        CurrentUserContext context = getCurrentUserContext();
        model.addAttribute("user", context.user());
        model.addAttribute("authorization", context.auth());
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByEmail(username);
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public User updateUserProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        validateProfileUpdate(request);

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Check if new email is already taken
            User existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser != null) {
                throw new InvalidUserParameterException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        log.info("Updating profile for user: {}", user.getEmail());
        return userRepository.save(user);
    }

    private void validateProfileUpdate(UpdateProfileRequest request) {
        if (request.getFirstName() != null && request.getFirstName().trim().isEmpty()) {
            throw new InvalidUserParameterException("First name cannot be empty");
        }
        if (request.getLastName() != null && request.getLastName().trim().isEmpty()) {
            throw new InvalidUserParameterException("Last name cannot be empty");
        }
        if (request.getEmail() != null) {
            String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
            if (!request.getEmail().matches(emailRegex)) {
                throw new InvalidUserParameterException("Invalid email format");
            }
        }
    }
}
