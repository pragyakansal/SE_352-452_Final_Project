package edu.kansal_wells_xu_pina.realestate_api.services;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;


public interface AuthService {
    User registerUser(User newUser);
    User loginUser(User existingUser);
}
