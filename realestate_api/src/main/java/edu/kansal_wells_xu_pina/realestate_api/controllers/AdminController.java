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
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "common-dashboard";
    }

    /* Redundant profile endpoint - now using common profile management
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit-profile")
    public String displayEditProfileForm() {
        return "edit-profile-form";
    }
    */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/manage-users")
    public String manageUsers(Model model) throws NotFoundException {
        List<User> allUsers = adminService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "manage-users-page";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{email}")
    public String deleteUserByEmailAddress(@PathVariable("email") String email) throws NotFoundException {
        String message = adminService.deleteUserByEmailAddress(email);
        return message;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create-agent")
    public String displayCreateAgentForm(Model model) {
        model.addAttribute("agentUser", new User());
        return "create-agent-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-agent")
    public String createAgent(@ModelAttribute User agentUser, Model model) throws AlreadyExistsException {
        User newUser = adminService.createAgentUser(agentUser);
        model.addAttribute("message", "Agent was created successfully");
        return "create-agent-form";
    }

}
