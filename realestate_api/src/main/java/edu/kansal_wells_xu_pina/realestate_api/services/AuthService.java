package edu.kansal_wells_xu_pina.realestate_api.services;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.utils.JwtAuthResponse;


public interface AuthService {
    User registerUser(User newUser);
    JwtAuthResponse loginUser(User existingUser);
}
