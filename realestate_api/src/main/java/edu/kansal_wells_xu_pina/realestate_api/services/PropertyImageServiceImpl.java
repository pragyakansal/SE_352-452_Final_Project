package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.InvalidPropertyImageParameterException;
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
        // Check if file is null
        if (file == null || file.isEmpty()) {
            throw new InvalidPropertyImageParameterException("Image file cannot be null");
        }

        // Allow only JPEG, PNG, or WEBP files
        String fileType = file.getContentType();
        if (fileType == null || !(fileType.equals("image/jpeg") || fileType.equals("image/png") || fileType.equals("image/webp"))) {
            throw new InvalidPropertyImageParameterException("Only JPEG, PNG, or WEBP images are valid");
        }
        // Allow only files up to 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new InvalidPropertyImageParameterException("File exceeds the max limit of 10MB");
        }

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

            PropertyImage propertyImage = new PropertyImage(imageFileName, property);

            // Add image to the prop
            property.addImage(propertyImage);

            // Save the prop image entity to the repo
            propertyImageRepository.save(propertyImage);

            log.info("Successfully saved image: {}", imageFileName);
            return imageFileName;
        } catch (IOException e) {
            log.error("Error storing property image:", e);
            throw new InvalidPropertyImageParameterException("Failed to store property image");
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


        // Delete the image file from the filesystem
        Path propertyFolder = baseImagePath.resolve(image.getProperty().getTitle());
        Path filePath = propertyFolder.resolve(image.getImageFileName());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting image file: {}", e.getMessage());
            throw new InvalidPropertyImageParameterException("Failed to delete image file");
        }

        // Remove the image from the property and delete the property image entity
        property.removeImage(image);
        propertyRepository.save(property);
        propertyImageRepository.delete(image);
    }

}
