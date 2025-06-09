package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.dtos.UpdateProfileRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

public interface UserService {

    void prepareDashboardModel(Model model);
    User getCurrentUser();
    User updateUserProfile(UpdateProfileRequest request);
}
