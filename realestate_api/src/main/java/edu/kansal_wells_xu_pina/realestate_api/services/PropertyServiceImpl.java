package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.dtos.PropertyFilterDto;
import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService{

    private final PropertyRepository propertyRepo;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepo) {
        this.propertyRepo = propertyRepo;
    }

    @Override
    public List<Property> findAll() {
        List<Property> properties = propertyRepo.findAll();
        if (properties.isEmpty()) {
            throw new NotFoundException("No artifacts found");
        }
        return properties;
    }

    @Override
    public Property findById(Long id) {
        return propertyRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("No property found with id: " + id));
    }

    @Override
    public Property updateProperty(Property savedProperty) {
        propertyRepo.save(savedProperty);
        return savedProperty;
    }

    @Override
    public List<Property> getFilteredProperties(PropertyFilterDto filter) {
        List<Property> allProperties = propertyRepo.findAll();

        List<Property> result = new ArrayList<>();
        for (Property property : allProperties) {
            boolean include = true;

            // Min square footage check
            if (filter.getMinSqFt() != null) {
                if (property.getSize() < filter.getMinSqFt()) {
                    include = false;
                }
            }

            // Min price check
            if (include && filter.getMinPrice() != null) {
                if (property.getPrice() < filter.getMinPrice()) {
                    include = false;
                }
            }

            // Max price check
            if (include && filter.getMaxPrice() != null) {
                if (property.getPrice() > filter.getMaxPrice()) {
                    include = false;
                }
            }

            if (include) {
                result.add(property);
            }
        }

        String sortBy = filter.getSortBy();

        if ("price_asc".equals(sortBy)) {
            // Lowest price first
            Collections.sort(result, (a, b) -> a.getPrice().compareTo(b.getPrice()));
        }
        else if ("price_desc".equals(sortBy)) {
            // Highest price first
            Collections.sort(result, (a, b) -> b.getPrice().compareTo(a.getPrice()));
        }
        else if ("sqft_asc".equals(sortBy)) {
            // Smallest size first
            Collections.sort(result, (a, b) -> a.getSize().compareTo(b.getSize()));
        }
        else if ("sqft_desc".equals(sortBy)) {
            // Largest size first
            Collections.sort(result, (a, b) -> b.getSize().compareTo(a.getSize()));
        }

        return result;
    }
}
