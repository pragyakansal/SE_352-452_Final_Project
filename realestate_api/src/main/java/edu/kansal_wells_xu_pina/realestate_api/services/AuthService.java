package edu.kansal_wells_xu_pina.realestate_api.services;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.utils.JwtAuthResponse;
import edu.kansal_wells_xu_pina.realestate_api.dtos.JwtResponse;
import edu.kansal_wells_xu_pina.realestate_api.jwt.JwtUtil;
import edu.kansal_wells_xu_pina.realestate_api.dtos.LoginRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service

public interface AuthService {

    JwtResponse authenticateAndGenerateToken(LoginRequest request);
    JwtAuthResponse loginUser(User user);
    User registerUser(User user);


}
