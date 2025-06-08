package edu.kansal_wells_xu_pina.realestate_api.repositories;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByAgent(User agent);
    Optional<Property> findById(Long propertyId);
    
    List<Property> findBySizeGreaterThanEqualAndPriceBetween(
        Integer minSqFt,
        Double minPrice,
        Double maxPrice,
        Sort sort
    );
}