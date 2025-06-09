package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.InvalidPropertyParameterException;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        validateProperty(savedProperty);
        propertyRepo.save(savedProperty);
        return savedProperty;
    }

    private void validateProperty(Property property) {
        if (property.getTitle() == null || property.getTitle().isEmpty()) {
            throw new InvalidPropertyParameterException("Property title cannot be null or empty");
        }
        if (property.getPrice() == null || property.getPrice() < 0) {
            throw new InvalidPropertyParameterException("Property price must be a positive value");
        }
        if (property.getDescription() == null || property.getDescription().isEmpty()) {
            throw new InvalidPropertyParameterException("Property description cannot be null or empty");
        }
        if (property.getLocation() == null || property.getLocation().isEmpty()) {
            throw new InvalidPropertyParameterException("Property location cannot be null or empty");
        }
        if (property.getSize() == null || property.getSize() <= 0) {
            throw new InvalidPropertyParameterException("Property size must be a positive value");
        }

    }
}
