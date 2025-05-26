package edu.kansal_wells_xu_pina.realestate_api.repositories;
import edu.kansal_wells_xu_pina.realestate_api.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
