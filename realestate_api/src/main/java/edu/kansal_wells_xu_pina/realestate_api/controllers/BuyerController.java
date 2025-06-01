package edu.kansal_wells_xu_pina.realestate_api.controllers;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.services.PropertyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    private final PropertyService propertyService;

    public BuyerController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

//    @PreAuthorize("hasRole('BUYER')")
    @GetMapping({"/listings", ""})
    public String viewListings(Model model) {
        List<Property> props = propertyService.findAll();
        model.addAttribute("properties", props);
        return "buyer/listings";
    }

//    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/listings/{id}")
    public String viewProperty(@PathVariable Long id, Model model) {
        Property property = propertyService.findById(id);
        model.addAttribute("property", property);
        return "buyer/propertydetail";
    }


}