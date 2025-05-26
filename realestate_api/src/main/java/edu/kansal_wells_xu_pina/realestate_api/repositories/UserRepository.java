package edu.kansal_wells_xu_pina.realestate_api.repositories;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Property, Long> {
    User findByEmail(String email);
}