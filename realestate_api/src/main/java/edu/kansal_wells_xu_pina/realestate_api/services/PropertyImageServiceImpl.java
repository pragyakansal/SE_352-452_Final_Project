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
    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        String projectRoot = System.getProperty("user.dir");
        Path uploadPath = Paths.get(projectRoot, uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        file.transferTo(filePath.toFile());

        return "/uploads/" + file.getOriginalFilename();

    }


    @Override
    public String storePropertyImage(Long propertyId, MultipartFile file) {
        try {

            Property property = propertyRepository.findById(propertyId).orElseThrow(()
                    -> new NotFoundException("Property not found"));
            String safeTitle = property.getTitle().replaceAll("[^a-zA-Z0-9]", "_");

            Path propertyFolder = baseImagePath.resolve(safeTitle);
            
            log.info("Saving image to folder: {}", propertyFolder.toAbsolutePath());
            Files.createDirectories(propertyFolder);

            String imageFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = propertyFolder.resolve(imageFileName);
            
            log.info("Saving image file: {}", filePath.toAbsolutePath());
            file.transferTo(filePath.toFile());

            // Save the image metadata in the database
            PropertyImage propertyImage = new PropertyImage();
            propertyImage.setImageFileName(imageFileName);
            propertyImage.setProperty(property);
            propertyImageRepository.save(propertyImage);

            // Save the image file name and associate it with the property
            property.addImage(propertyImage);
            propertyRepository.save(property);

            log.info("Successfully saved image: {}", imageFileName);
            return imageFileName;
        } catch (IOException e) {
            log.error("Error storing property image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store property image", e);
        }
    }

    @Override
    public List<String> storeMultiplePropertyImages(Long propertyId, List<MultipartFile> images) {
        List<String> uploadedFileNames = new ArrayList<>();
        for (MultipartFile file : images) {
            String fileName = storePropertyImage(propertyId, file);
            uploadedFileNames.add(fileName);
        }
        return uploadedFileNames;
    }

}
