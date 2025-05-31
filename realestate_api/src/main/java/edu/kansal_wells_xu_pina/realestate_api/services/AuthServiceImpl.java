package edu.kansal_wells_xu_pina.realestate_api.services;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.InvalidUserParameterException;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.AlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {

    private UserRepository userRepository;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User newUser) {
        User existingUser = userRepository.findByEmail(newUser.getEmail());
        if (existingUser != null) {
            throw new AlreadyExistsException("A user with the email: " + newUser.getEmail() + " already exists. Please login " +
                    " to your existing account or create an account with a new email.");
        }
        validateUserRegistrationFields(newUser);
        return newUser;
    }

    public User loginUser(User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser == null) {
            throw new InvalidUserParameterException("A user with the email: " + user.getEmail() + " could not be found in the database. " +
                    " Please try again and login with an email that is already registered within our system.");
        }

        // Temporary password check until security part is implemented
        // TO-DO (for Edith): compare encrypted passwords instead of plaintext passwords
        if (!existingUser.getPassword().matches(user.getPassword())) {
            throw new InvalidUserParameterException("The password entered is incorrect. Please try again with the correct password.");
        }
        return existingUser;
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

        // Correct format for email
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
