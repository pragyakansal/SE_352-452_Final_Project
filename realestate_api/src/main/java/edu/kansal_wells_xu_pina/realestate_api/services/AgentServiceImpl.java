package edu.kansal_wells_xu_pina.realestate_api.services;


import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
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



    @Autowired
    public AgentServiceImpl(UserRepository userRepository, PropertyRepository propertyRepository, PropertyImageRepository imageRepository, PropertyImageService propertyImageService) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.propertyImageRepository = imageRepository;
        this.propertyImageService = propertyImageService;

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
    public Property addNewProperty(Property newProperty, List<MultipartFile> imageFiles) {
        User agent = getCurrentAgent();
        newProperty.setAgent(agent);

        // Save the property first to generate an ID
        newProperty = propertyRepository.save(newProperty);

        // Now handle the images
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    try {
                        //String imagePath = propertyImageRepository.saveImage(file);
                        String imageFileName = propertyImageService.storePropertyImage(newProperty.getId(), file);
                        PropertyImage propertyImage = new PropertyImage(imageFileName, newProperty);
                        propertyImageRepository.save(propertyImage);
                        newProperty.getImages().add(propertyImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to store property image" + file.getOriginalFilename(), e);
                    }
                }
            }
            // Save the property again with images
            newProperty = propertyRepository.save(newProperty);
        }

        return newProperty;
         /*
            Set<PropertyImage> images = imageFiles.stream()
                    .map(file -> {
                        PropertyImage propertyImage = new PropertyImage();
                        propertyImage.setImageFileName(file.getOriginalFilename());
                        propertyImage.setProperty(newProperty);
                        return propertyImageRepository.save(propertyImage);
                    })
                    .collect(Collectors.toSet());
            newProperty.setImages(images);
        }

          */

        // List<PropertyImage> propertyImages = newProperty.getImages();



        /*List<PropertyImage> images = propertyImages.retainAll().stream()
                .map(image -> {
                    PropertyImage foundImage = propertyImageRepository.findByImageFileName(image.getImageFileName());
                    if (foundImage == null) {
                        throw new NotFoundException("Image not found: " + image.getImageFileName());
                    }
                    return foundImage;
                })
                .collect(Collectors.toSet   ());
        newProperty.setImages((List<PropertyImage>) images);

         */
        //return propertyRepository.save(newProperty);

    }

    /*
    @Override
    public Property editProperty(Property property) {
        getAgentPropertyById(agentId, propertyId);
        property.setAgent(getAgentById(agentId));
        return propertyRepository.save(property);
    }

     */

    // @Override
    // TODO: Implement DeleteProperty method
    // public void DeleteProperty(Long agentId, Long propertyId) {
    //     Property property = getAgentPropertyById(agentId, propertyId);
    //     propertyRepository.delete(property);
    // }

    @Override
    public String deletePropertyByPropertyId(Long propertyId, Long agentId) {
        Property propertyToDelete = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NotFoundException("The property with the id: " + propertyId + " could not be found." +
                "Please delete an existing property."));

        if (!propertyToDelete.getAgent().getId().equals(agentId)) {
            throw new IllegalArgumentException("Agents are only authorized to delete their own properties.");
        }
        propertyRepository.delete(propertyToDelete);
        return "The property with the ID: " + propertyId + " was deleted successfully.";
    }


    // ? PropertyImageRepository methods can be added here



}