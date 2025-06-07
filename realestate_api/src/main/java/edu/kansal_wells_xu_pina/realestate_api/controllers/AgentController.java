package edu.kansal_wells_xu_pina.realestate_api.controllers;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.services.AgentService;
import edu.kansal_wells_xu_pina.realestate_api.services.PropertyImageService;
import edu.kansal_wells_xu_pina.realestate_api.services.PropertyService;
import edu.kansal_wells_xu_pina.realestate_api.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
@RequestMapping("/agent")
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);
    private final AgentService agentService;
    private final UserService userService;
    private final PropertyImageService propertyImageService;
    private final PropertyService propertyService;

    @Autowired
    public AgentController(AgentService agentService, UserService userService, PropertyImageService propertyImageService,
                           PropertyService propertyService) {
        this.agentService = agentService;
        this.userService = userService;
        this.propertyImageService = propertyImageService;
        this.propertyService = propertyService;
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/managelistings")
    public String manageListings(Model model) {
        User currentUser = userService.getCurrentUser();
        List<Property> properties = agentService.getAgentProperties(currentUser.getId());
        model.addAttribute("agent", currentUser.getFirstName() + " " + currentUser.getLastName());
        model.addAttribute("properties", properties);
        return "agent/managelistings";
    }

    // === ADD PROPERTY ===
    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/addproperty")
    public String addPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "agent/addproperty";
    }

    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/addproperty")
    public String addNewProperty(@ModelAttribute("property") Property newProperty,
                                 @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                 RedirectAttributes redirectAttributes, Model model) {
        try {
            //First, add the property (this will assign it an ID)
            Property savedProperty = agentService.addNewProperty(newProperty, files);
            redirectAttributes.addFlashAttribute("successMessage", "Property added successfully");
            // model.addAttribute("property", savedProperty);
            return "redirect:/agent/managelistings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add property: " + e.getMessage());
            return "redirect:/agent/addproperty";
        }


            /*

            // Then store the property image (if uploaded) and update the property record
            if (files != null) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        propertyImageService.storeMultiplePropertyImages(savedProperty.getId(), (List<MultipartFile>) file);
                    }
                }
            }

            model.addAttribute("property", savedProperty);
            return "redirect:agent/managelistings";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add property: " + e.getMessage());
            return "redirect:/agent/addproperty";
        }

             */
    }

    @PreAuthorize("hasRole('AGENT')")
    @DeleteMapping("/delete/{propertyId}")
    public String deletePropertyByPropertyId(@PathVariable("propertyId") Long propertyId) throws NotFoundException {
        log.info("Received request to delete property with ID: " + propertyId);
        log.info("In delete property controller method");
        try {
            User currentAgent = userService.getCurrentUser();
            String message = agentService.deletePropertyByPropertyId(propertyId, currentAgent.getId());
            log.info(message);
            return "redirect:/agent/managelistings";
        } catch (Exception e) {
            log.error("Error in deleting property with id {}: {}", propertyId, e.getMessage());
            return "redirect:/agent/managelistings?error=true";
        }
    }


    // === EDIT PROPERTY ===
    // TO-DO: template for editproperty.html and editProperty and saveProperty method to handle the form submission
    // @PreAuthorize("hasRole('AGENT')")
   /*
    @PostMapping("/editproperty")
    public String editProperty(@ModelAttribute Property property, Model model) {
        return "temp";
    }
    */

 }
