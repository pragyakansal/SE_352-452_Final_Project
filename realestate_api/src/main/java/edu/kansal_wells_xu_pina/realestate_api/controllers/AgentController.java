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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
        
        // Log property images
        for (Property property : properties) {
            log.info("Property: {} has {} images", property.getTitle(), 
                    property.getImages() != null ? property.getImages().size() : 0);
            if (property.getImages() != null && !property.getImages().isEmpty()) {
                log.info("First image filename: {}", property.getImages().get(0).getImageFileName());
            }
        }
        
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
        log.info("Received request to add new property: {}", newProperty.getTitle());
        try {
            //First, add the property (this will assign it an ID)
            log.info("Calling agentService.addNewProperty");
            Property savedProperty = agentService.addNewProperty(newProperty, files);
            log.info("Property saved successfully with ID: {}", savedProperty.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Property added successfully");
            log.info("Redirecting to /agent/managelistings");
            return "redirect:/agent/managelistings";
        } catch (Exception e) {
            log.error("Failed to add property: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to add property: " + e.getMessage());
            return "redirect:/agent/addproperty";
        }
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

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/editproperty/{id}")
    public String editPropertyForm(@PathVariable Long id, Model model)  {
        Property property = propertyService.findById(id);
        model.addAttribute("property", property);
        return "agent/editproperty";
    }

    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/editproperty/{id}")
    public String editProperty(@PathVariable("id") Long id,
                               @ModelAttribute Property updatedProperty,
                               @RequestParam("title") String title,
                               @RequestParam("price") Double price,
                               @RequestParam("description") String description,
                               @RequestParam("location") String location,
                               @RequestParam("size") Integer size,
                               @RequestParam(value = "files", required = false) List<MultipartFile> files,
                               RedirectAttributes redirectAttributes, Model model) {
        try {
            // Look up the existing property to get the correct ID
            Property actualProp = propertyService.findById(id);

            // Copy updates from form-bound property
            actualProp.setTitle(updatedProperty.getTitle());
            actualProp.setPrice(updatedProperty.getPrice());
            actualProp.setDescription(updatedProperty.getDescription());
            actualProp.setLocation(updatedProperty.getLocation());
            actualProp.setSize(updatedProperty.getSize());

            agentService.updateProperty(actualProp, title, price, description, location, size);

            // Save new images if provided, iterates over new uploads and stores/creates a new Prop Image and adds to list for updating property
            if (files != null && !files.isEmpty()) {
                List<PropertyImage> newUpload = new ArrayList<>();
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        try {
                            String imagefilename = propertyImageService.storePropertyImage(actualProp.getId(), file);
                            // PropertyImage propertyImage = new PropertyImage(imagefilename, actualProp);
                            // newUpload.add(propertyImage);
                        } catch (Exception e) {
                            log.error("Error storing property image: {}", e.getMessage(), e);
                            throw new RuntimeException("Failed to store property image: " + file.getOriginalFilename(), e);
                        }
                    }
                }
                actualProp.getImages().addAll(newUpload);
                propertyService.updateProperty(actualProp);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Property updated successfully");
            return "redirect:/agent/managelistings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update property: " + e.getMessage());
            return "redirect:/agent/editproperty/" + id;
        }
    }

    /*
    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/deleteimage/{propertyId}/{imageId}")
    public String deletePropImage(@PathVariable("propertyId") Long propertyId,
                                      @PathVariable("imageId") Long imageId,
                                      RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getCurrentUser();
            Property property =
            agentService.deletePropertyImage(propertyId, imageId);
            redirectAttributes.addFlashAttribute("successMessage", "Image deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete image: " + e.getMessage());
        }
        return "redirect:/agent/editproperty/" + propertyId;
    }

     */




 }
