package edu.kansal_wells_xu_pina.realestate_api.utils;

import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import org.springframework.security.core.Authentication;

public record CurrentUserContext(User user, Authentication auth) {}
