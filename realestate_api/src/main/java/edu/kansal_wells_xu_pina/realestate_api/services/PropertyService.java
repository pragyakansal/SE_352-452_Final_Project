package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;

import java.util.List;

public interface PropertyService {
    List<Property> findAll();

    Property findById(Long id);
}
