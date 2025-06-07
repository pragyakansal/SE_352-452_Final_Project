package edu.kansal_wells_xu_pina.realestate_api.controllers;
import edu.kansal_wells_xu_pina.realestate_api.enums.Role;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.AlreadyExistsException;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.jwt.JwtUtil;
import edu.kansal_wells_xu_pina.realestate_api.services.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
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
        log.info("Message: ", message);
        return "redirect:/admin/manage-users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create-agent")
    public String displayCreateAgentForm(Model model) {
        model.addAttribute("agentUser", new User());
        return "admin/create-agent-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-agent")
    public String createAgent(@ModelAttribute User agentUser, Model model) throws AlreadyExistsException {
        try {
            agentUser.setRole(Role.AGENT);
            adminService.createAgentUser(agentUser);
            model.addAttribute("message", "Agent was created successfully");
            return "redirect:/landing-page/dashboard";
        } catch (Exception e) {
            log.error("Error in creating agent: " + agentUser.getEmail() + ", role: " + agentUser.getRole(),  e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("agentUser", agentUser);
            return "admin/create-agent-form";
        }
    }

}
