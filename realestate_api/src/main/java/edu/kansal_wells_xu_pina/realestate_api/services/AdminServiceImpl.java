package edu.kansal_wells_xu_pina.realestate_api.services;
import org.springframework.stereotype.Service;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import java.util.List;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.BadParameterException;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.AlreadyExistsException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

    private UserRepository userRepository;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return userRepository.save(newAgentUser);
    }

    @Override
    @Transactional
    public User updateAdminProfile(User updatedAdminUser) {
        User existingAdminUser = userRepository.findByEmail(updatedAdminUser.getEmail());
        if (existingAdminUser == null) {
            throw new NotFoundException("The admin with the email address: " + updatedAdminUser.getEmail() + " could not be found in the database." +
                    " You can only update an admin with an existing email address. Please try again.");
        }
        validateEditProfileFormFields(updatedAdminUser);
        return userRepository.save(updatedAdminUser);

    }

    private void validateEditProfileFormFields(User adminUser) {
        if (adminUser.getFirstName() == null || adminUser.getFirstName().trim().isEmpty()) {
            throw new BadParameterException("The admin's first name should not be null or empty. Please try again with a valid input.");
        }

        if (adminUser.getLastName() == null || adminUser.getLastName().trim().isEmpty()) {
            throw new BadParameterException("The admin's last name should not be null or empty. Please try again with a valid input.");
        }

        if (adminUser.getEmail() == null || adminUser.getEmail().trim().isEmpty()) {
            throw new BadParameterException("The admin's email must contain an @ symbol, and it should not be null or empty. " +
                    " Please try again with a valid input.");
        }

        // Correct format for email
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!adminUser.getEmail().matches(emailRegex)) {
            throw new BadParameterException("The email format is incorrect. Please try again with a valid input.");
        }
    }

    private void validateAgentFields(User agentUser) {
        if (agentUser.getFirstName() == null || agentUser.getFirstName().trim().isEmpty()) {
            throw new BadParameterException("The agent's first name should not be null or empty. Please try again with a valid input.");
        }

        if (agentUser.getLastName() == null || agentUser.getLastName().trim().isEmpty()) {
            throw new BadParameterException("The agent's last name should not be null or empty. Please try again with a valid input.");
        }

        if (agentUser.getEmail() == null || agentUser.getEmail().trim().isEmpty()) {
            throw new BadParameterException("The agent's email must contain an @ symbol, and it should not be null or empty. " +
                    " Please try again with a valid input.");
        }

        // Correct format for email
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!agentUser.getEmail().matches(emailRegex)) {
            throw new BadParameterException("The email format is incorrect. Please try again with a valid input.");
        }

        if (agentUser.getPassword() == null || agentUser.getPassword().length() < 7 || agentUser.getPassword().length() > 14 || !agentUser.getPassword().matches(".*[^a-zA-Z0-9].*")) {
            throw new BadParameterException("The agent's password must contain at least 7 characters and at most 14 characters, " +
                    " and it must have at least one special character. Please try again with a valid input.");
        }
    }
}
