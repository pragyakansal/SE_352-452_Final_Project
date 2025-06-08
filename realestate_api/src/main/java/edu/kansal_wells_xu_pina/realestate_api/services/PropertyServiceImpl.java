package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.dtos.PropertyFilterDto;
import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        Sort sort = Sort.unsorted();
        if (filter.getSortBy() != null && !filter.getSortBy().isEmpty()) {
            switch (filter.getSortBy()) {
                case "price_asc":
                    sort = Sort.by("price").ascending();
                    break;
                case "price_desc":
                    sort = Sort.by("price").descending();
                    break;
                case "sqft_asc":
                    sort = Sort.by("size").ascending();
                    break;
                case "sqft_desc":
                    sort = Sort.by("size").descending();
                    break;
            }
        }

        // If no filters are set, return all properties with sorting
        if (filter.getMinSqFt() == null && filter.getMinPrice() == null && filter.getMaxPrice() == null) {
            return propertyRepo.findAll(sort);
        }

        // Use the repository method with the provided filters
        return propertyRepo.findBySizeGreaterThanEqualAndPriceGreaterThanEqualAndPriceLessThanEqual(
            filter.getMinSqFt() != null ? filter.getMinSqFt() : 0,
            filter.getMinPrice() != null ? filter.getMinPrice() : 0.0,
            filter.getMaxPrice() != null ? filter.getMaxPrice() : Double.MAX_VALUE,
            sort
        );
    }
}
