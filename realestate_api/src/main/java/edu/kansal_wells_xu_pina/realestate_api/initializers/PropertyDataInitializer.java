package edu.kansal_wells_xu_pina.realestate_api.initializers;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class PropertyDataInitializer {
    private final PropertyRepository propertyRepository;

    public PropertyDataInitializer(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }



}
