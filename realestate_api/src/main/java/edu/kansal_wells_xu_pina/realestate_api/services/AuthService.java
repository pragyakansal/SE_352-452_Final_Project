package edu.kansal_wells_xu_pina.realestate_api.services;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.utils.JwtAuthResponse;
import edu.kansal_wells_xu_pina.realestate_api.dtos.JwtResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;


public interface AuthService {

//    JwtResponse authenticateAndGenerateToken(LoginRequest request);
    JwtAuthResponse loginUser(User user);
    User registerUser(User user);
    JwtResponse authenticateAndGenerateToken(User user);

    public Cookie loginAndCreateJwtCookie(User user) throws BadCredentialsException;

    void clearJwtCookie(HttpServletResponse response);


}