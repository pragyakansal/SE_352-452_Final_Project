package edu.kansal_wells_xu_pina.realestate_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
