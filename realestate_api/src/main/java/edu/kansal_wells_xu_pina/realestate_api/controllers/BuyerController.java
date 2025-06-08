package edu.kansal_wells_xu_pina.realestate_api.controllers;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.dtos.PropertyFilterDto;
import edu.kansal_wells_xu_pina.realestate_api.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    private final PropertyService propertyService;

    @Autowired
    public BuyerController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping({"/listings", ""})
    public String viewListings(@ModelAttribute PropertyFilterDto filter, Model model) {
        // Convert empty strings to null for numeric fields
        if (filter.getMinSqFt() != null && filter.getMinSqFt() == 0) {
            filter.setMinSqFt(null);
        }
        if (filter.getMinPrice() != null && filter.getMinPrice() == 0) {
            filter.setMinPrice(null);
        }
        if (filter.getMaxPrice() != null && filter.getMaxPrice() == 0) {
            filter.setMaxPrice(null);
        }
        
        model.addAttribute("properties", propertyService.getFilteredProperties(filter));
        model.addAttribute("filter", filter);
        return "buyer/listings";
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/listings/{id}")
    public String viewProperty(@PathVariable Long id, Model model) {
        Property property = propertyService.findById(id);
        model.addAttribute("property", property);
        return "buyer/propertydetail";
    }
}