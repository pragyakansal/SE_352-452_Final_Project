package edu.kansal_wells_xu_pina.realestate_api.repositories;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}