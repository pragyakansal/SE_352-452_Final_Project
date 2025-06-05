package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;

import java.io.IOException;
import java.util.List;

public interface PropertyService {
    List<Property> findAll();

    Property findById(Long id);

    Property updateProperty(Property savedProperty);

}
