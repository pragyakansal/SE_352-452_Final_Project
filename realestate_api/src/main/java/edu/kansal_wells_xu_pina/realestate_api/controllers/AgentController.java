package edu.kansal_wells_xu_pina.realestate_api.controllers;


import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.services.AgentService;
import org.aspectj.weaver.loadtime.Agent;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/agent")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {this.agentService = agentService;}


    @PreAuthorize("hasRole('AGENT')")
    @GetMapping({"/dashboard", "/"})
    public String agentDashboard(Model model) {
        model.addAttribute("agent", agentService.getCurrentAgent());
        return "agent-dashboard";
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/myprofile/{id}")
    public String myAgentProfile(@PathVariable Long id, Model model) {
        model.addAttribute("agent", agentService.getCurrentAgent());
        return "showagentprofile";
    }


    @PreAuthorize("hasRole('AGENT' + ' or hasRole('ADMIN')")
    @GetMapping({"editprofile", "/editprofile/{id}"})
    public String editAgentProfile(@PathVariable(required = false) Long id, Model model) {
        if (id != null && id > 0) {
            model.addAttribute("agent", agentService.getAgent(id));
        } else {
            model.addAttribute("agent", new Agent());
        }
        return "edditagentprofile";
    }


    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/managelistings")
    public String manageListings(Model model) {
        model.addAttribute("properties", agentService.getAllProperties());
        return "agentmanagelistings";
    }

    // TO-DO: template for agentaddproperty.html and addProperty method to handle the form submission
    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/addproperty")
    public String addProperty(Model model) {
        model.addAttribute("property", new Property());
        return "agentaddproperty";
    }


    // TO-DO: template for editproperty.html and editProperty and saveProperty method to handle the form submission
    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/editproperty/{id}")
    public String editProperty(@ModelAttribute Property property, Model model) {
        return "temp";
    }





}
