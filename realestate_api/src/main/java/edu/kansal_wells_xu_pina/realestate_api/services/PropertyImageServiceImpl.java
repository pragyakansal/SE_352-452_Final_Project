package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyImageRepository;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyRepository;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PropertyImageServiceImpl implements PropertyImageService {

    private static final Logger log = LoggerFactory.getLogger(PropertyImageServiceImpl.class);

    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final Path baseImagePath;


    @Autowired
    public PropertyImageServiceImpl(PropertyRepository propertyRepository, PropertyImageRepository propertyImageRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyImageRepository = propertyImageRepository;
        this.baseImagePath = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/images/property_images");
    }

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public String storePropertyImage(Long propertyId, MultipartFile file) {
        try {
            // Locate property
            Property property = propertyRepository.findById(propertyId).orElseThrow(()
                    -> new NotFoundException("Property not found"));

            Path propertyFolder = baseImagePath.resolve(property.getTitle());

            log.info("Saving image to folder: {}", propertyFolder.toAbsolutePath());
            Files.createDirectories(propertyFolder);

            String imageFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = propertyFolder.resolve(imageFileName);

            log.info("Saving image file: {}", filePath.toAbsolutePath());
            file.transferTo(filePath.toFile());

            // Create property image entity
            PropertyImage propertyImage = new PropertyImage(imageFileName, property);

            // Add image to the prop
            property.addImage(propertyImage);

            // Save the prop image entity to the repo
            propertyImageRepository.save(propertyImage);

            log.info("Successfully saved image: {}", imageFileName);
            return imageFileName;
        } catch (IOException e) {
            log.error("Error storing property image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store property image", e);
        }
    }

    @Override
    public void deletePropertyImage(Long propertyId, Long imageId) {
        // Locate property
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NotFoundException("Property not found with id: " + propertyId));
        // Locate image
        PropertyImage image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Image not found with id: " + imageId));

        /*
        // Delete the image file from the filesystem
        Path propertyFolder = baseImagePath.resolve(image.getProperty().getTitle());
        Path filePath = propertyFolder.resolve(image.getImageFileName());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting image file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete image file", e);
        }

         */

        // Remove the image from the property and delete the entity
        property.removeImage(image);
        propertyRepository.save(property);
        propertyImageRepository.delete(image);
    }

}
