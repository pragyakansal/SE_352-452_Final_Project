package edu.kansal_wells_xu_pina.realestate_api.repositories;

import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
    PropertyImage findByImageFileName(String imageFile) ;
}