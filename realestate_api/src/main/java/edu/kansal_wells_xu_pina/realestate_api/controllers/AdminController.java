package edu.kansal_wells_xu_pina.realestate_api.controllers;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.AlreadyExistsException;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.services.AdminService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "dashboard";
    }

    @GetMapping("/edit-profile")
    public String displayEditProfileForm() {
        return "edit-profile-form";
    }

    @PutMapping("/edit-profile")
    public User editAdminProfile(@RequestBody User adminUser) throws NotFoundException {

    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model) throws NotFoundException {
        List<User> allUsers = adminService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "manage-users-page";
    }

    @DeleteMapping("/delete/{email}")
    public String deleteUserByEmailAddress(@PathVariable("email") String email) throws NotFoundException {
        String message = adminService.deleteUserByEmailAddress(email);
        return message;
    }

    @PostMapping("/create-agent")
    public String createAgent(@RequestBody User agentUser) throws AlreadyExistsException {
        User newUser = adminService.createAgentUser(agentUser);
        return "create-agent-form";
    }

}
