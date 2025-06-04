package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

public interface UserService {

//    @PreAuthorize("isAuthenticated()")
    void prepareDashboardModel(Model model);

//    @PreAuthorize("isAuthenticated()")
    User getCurrentUser();
}
