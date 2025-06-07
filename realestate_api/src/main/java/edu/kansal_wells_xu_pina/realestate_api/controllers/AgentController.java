package edu.kansal_wells_xu_pina.realestate_api.controllers;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.services.AgentService;
import edu.kansal_wells_xu_pina.realestate_api.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/agent")
public class AgentController {

    private final AgentService agentService;
    private final UserService userService;

    public AgentController(AgentService agentService, UserService userService) {this.agentService = agentService;
        this.userService = userService;
    }

   /* @PreAuthorize("hasRole('AGENT')")
    @GetMapping({"/dashboard", "/"})
    public String agentDashboard() {
        return "common-dashboard";
    }

    */

    /* Redundant profile endpoints - now using common profile management
    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/{id}/myprofile")
    public String myAgentProfile(@PathVariable Long id, Model model) {
        model.addAttribute("agent", agentService.getAgentById(id));
        return "agentprofile";
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/{id}/editprofile")
    public String editAgentProfile(@PathVariable(required = false) Long id, Model model) {
        if (id != null && id > 0) {
            model.addAttribute("agent", agentService.getAgentDtoById(id));
        } else {
            model.addAttribute("agent", new UpdateProfileRequest());
        }
        return "editagentprofile";
    }
    */

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/managelistings")
    public String manageListings(Model model) {
        User currentUser = userService.getCurrentUser();
        List<Property> properties = agentService.getAgentProperties(currentUser.getId());
        model.addAttribute("agent", currentUser.getFirstName() + " " + currentUser.getLastName());
        model.addAttribute("properties", properties);
        return "agent/managelistings";
    }

    // TO-DO: template for agentaddproperty.html and addProperty method to handle the form submission
    //@PreAuthorize("hasRole('AGENT')")
    @GetMapping("/addproperty")
    public String addPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "agent/addproperty";
    }

    // @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/{id}/addproperty")
    public String addNewProperty(@PathVariable Long id, @ModelAttribute Property newProperty, Model model) {
        agentService.addNewProperty(newProperty);
        model.addAttribute("property", newProperty);
        return "redirect:/agent/" + id + "/managelistings";
    }


    // TO-DO: template for editproperty.html and editProperty and saveProperty method to handle the form submission
    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/editproperty/{id}")
    public String editProperty(@ModelAttribute Property property, Model model) {
        return "temp";
    }


}