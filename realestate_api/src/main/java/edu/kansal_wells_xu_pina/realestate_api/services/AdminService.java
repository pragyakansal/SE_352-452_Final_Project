package edu.kansal_wells_xu_pina.realestate_api.services;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import java.util.List;


public interface AdminService {
    List<User> getAllUsers();
    String deleteUserByEmailAddress(String email);
    User createAgentUser(User newAgentUser);
    User updateAdminProfile(User updatedAdminUser);
}
