package edu.kansal_wells_xu_pina.realestate_api.initializers;
import edu.kansal_wells_xu_pina.realestate_api.enums.Role;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class UserDataInitializer {
    private final UserRepository userRepository;

    public UserDataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() != 0) {
            System.out.println("Data already present - not executing initializer.");
            return;
        }
        System.out.println("Initializing test data");
        User jane = new User("Jane", "Doe", "janedoe@gmail.com", "janedoe123#", LocalDateTime.now(), Role.ADMIN);
        User ellen = new User("Ellen", "Smith", "ellensmith111@gmail.com", "vR@85mNc", LocalDateTime.now(), Role.ADMIN);
        userRepository.saveAll(Arrays.asList(jane, ellen));

    }

}
