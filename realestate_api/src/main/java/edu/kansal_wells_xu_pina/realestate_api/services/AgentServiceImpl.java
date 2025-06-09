package edu.kansal_wells_xu_pina.realestate_api.services;


import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.InvalidPropertyImageParameterException;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.InvalidUserParameterException;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyImageRepository;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyRepository;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AgentServiceImpl implements AgentService {


    private final UserRepository userRepository;

    private final PropertyRepository propertyRepository;

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyImageService propertyImageService;
    private final PropertyService propertyService;



    @Autowired
    public AgentServiceImpl(UserRepository userRepository, PropertyRepository propertyRepository,
                            PropertyImageRepository imageRepository, PropertyImageService propertyImageService, PropertyService propertyService) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.propertyImageRepository = imageRepository;
        this.propertyImageService = propertyImageService;
        this.propertyService = propertyService;

    }


    @Override
    public User getAgentById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agent not found with id: " + id));
        if (!user.getRole().equals(Role.AGENT)) {
            throw new InvalidUserParameterException("User: " + id + " is not an agent.");
        }
        return user;
    }

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
            throw new IllegalArgumentException("Property id: " + propertyId + " does not belong to agent id: " + agentId);
        }
        return property;
    }

    private User getCurrentAgent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User agent = userRepository.findByEmail(email);
        if (agent == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }
        if (!agent.getRole().equals(Role.AGENT)) {
            throw new IllegalArgumentException("User is not an agent.");
        }
        return agent;
    }

    @Override
    public Property addNewProperty(Property newProperty, List<MultipartFile> imageFiles) {
        User agent = getCurrentAgent();
        newProperty.setAgent(agent);

        // Save the property to generate ID
        newProperty = propertyRepository.save(newProperty);

        //  Handle images
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    try {
                        String imageFileName = propertyImageService.storePropertyImage(newProperty.getId(), file);
                    } catch (InvalidPropertyImageParameterException e) {
                        throw new RuntimeException("Failed to store property image" + file.getOriginalFilename(), e);
                    }
                }
            }
            // Save the property with images
            newProperty = propertyRepository.save(newProperty);
        }
        return newProperty;
    }

    @Override
    public Property getCurrentProperty(Long propertyId) {
        return propertyService.findById(propertyId);
    }

    @Override
    public void updateProperty(Property propertyToUpdate, String title, Double price, String description,
                                      String location, Integer size) {
        Property property = propertyService.findById(propertyToUpdate.getId());

        property.setTitle(title);
        property.setPrice(price);
        property.setDescription(description);
        property.setLocation(location);
        property.setSize(size);

        propertyRepository.save(property);
    }


    @Override
    public String deletePropertyByPropertyId(Long propertyId, Long agentId) {
        Property propertyToDelete = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NotFoundException("The property with the id: " + propertyId + " could not be found." +
                "Please delete an existing property."));

        if (!propertyToDelete.getAgent().getId().equals(agentId)) {
            throw new IllegalArgumentException("Agents are only authorized to delete their own properties.");
        }

        if (propertyToDelete.getImages() != null) {
            for (PropertyImage image : propertyToDelete.getImages()) {
                propertyImageRepository.delete(image);
            }
        }

        propertyRepository.delete(propertyToDelete);
        return "The property with the ID: " + propertyId + " was deleted successfully.";
    }

}