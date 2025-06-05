package edu.kansal_wells_xu_pina.realestate_api.services;


import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyImageRepository;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyRepository;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.enums.Role;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PropertyRepository propertyRepository;

    @Autowired
    private final PropertyImageRepository propertyImageRepository;
    private final UserService userService;


    public AgentServiceImpl(UserRepository userRepository, PropertyRepository propertyRepository, PropertyImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.propertyImageRepository = imageRepository;
        this.userService = userService;
    }


    @Override
    public User getAgentById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agent not found with id: " + id));
        if (!user.getRole().equals(Role.AGENT)) {
            throw new IllegalArgumentException("User: " + id + " is not an agent.");
        }
        return user;
    }

//    @Override
//    public UpdateProfileRequest getAgentDtoById(Long id) {
//        User agent = getAgentById(id);
//        return new UpdateProfileRequest(agent.getFirstName(), agent.getLastName(), agent.getEmail());
//    }
//
//    @Override
//    public User editAgentProfile(Long agentId, UpdateProfileRequest request) {
//        User agent = getAgentById(agentId);
//       if (request.getFirstName() != null) {
//            agent.setFirstName(request.getFirstName());
//        }
//        if (request.getLastName() != null) {
//            agent.setLastName(request.getLastName());
//        }
//        if (request.getEmail() != null) {
//            agent.setEmail(request.getEmail());
//        }
//        return userRepository.save(agent);
//    }

    @Override
    public List<Property> getAgentProperties(Long agentId) {
        User agent = getAgentById(agentId);
        return propertyRepository.findByAgent(agent);
    }

    @Override
    public Property getAgentPropertyById(Long agentId, Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NotFoundException("Property not found with id: " + propertyId));
        if (!property.getAgent().getId().equals(agentId)) {
            throw new IllegalArgumentException("Property with id: " + propertyId + " does not belong to agent with id: " + agentId);
        }
        return property;
    }

    /*
    @Override

    public Property addNewProperty(Property property) {
        User agent =  userService.getCurrentUser();
        if (!agent.getRole().equals(Role.AGENT)) {
            throw new IllegalArgumentException("User is not an agent.");
        }
        property.setAgent(agent);
        Property savedProperty = propertyRepository.save(property);

        for (PropertyImage image : property.getImages()) {}
        return propertyRepository.save(property);


    }
        User agent = getAgentById(agentId);
        property.setAgent(agent);
        return propertyRepository.save(property);
    }

     */

    @Override
    public Property addNewProperty(Property newProperty) {
        User agent = userService.getCurrentUser();
        if (!agent.getRole().equals(Role.AGENT)) {
            throw new IllegalArgumentException("User is not an agent.");
        }
        newProperty.setAgent(agent);
        Set<PropertyImage> images = newProperty.getImages().stream()
                .map(image -> {
                    PropertyImage foundImage = propertyImageRepository.findByImageFileName(image.getImageFileName());
                    if (foundImage == null) {
                        throw new NotFoundException("Image not found: " + image.getImageFileName());
                    }
                    return foundImage;
                })
                .collect(Collectors.toSet());
        return propertyRepository.save(newProperty);

    }

    @Override
    public Property EditProperty(Long agentId, Long propertyId, Property property) {
        getAgentPropertyById(agentId, propertyId);
        property.setAgent(getAgentById(agentId));
        return propertyRepository.save(property);
    }

    // @Override
    // TODO: Implement DeleteProperty method
    // public void DeleteProperty(Long agentId, Long propertyId) {
    //     Property property = getAgentPropertyById(agentId, propertyId);
    //     propertyRepository.delete(property);
    // }


    // ? PropertyImageRepository methods can be added here



}
