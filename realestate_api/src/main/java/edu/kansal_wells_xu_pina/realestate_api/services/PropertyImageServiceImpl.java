package edu.kansal_wells_xu_pina.realestate_api.services;

import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyImageRepository;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyRepository;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
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


    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;


    @Autowired
    public PropertyImageServiceImpl(PropertyRepository propertyRepository, PropertyImageRepository propertyImageRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyImageRepository = propertyImageRepository;

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
            String imageFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            // Path uploadPath = Paths.get(System.getProperty("user.dir"),"uploads", "images", "property_images");
            Property property = propertyRepository.findById(propertyId).orElseThrow(()
                    -> new NotFoundException("Property not found"));
            String safeTitle = property.getTitle().replaceAll("[^a-zA-Z0-9]", "_");
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploadDir", safeTitle);
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(imageFileName);
            file.transferTo(filePath.toFile());

            // Property property = propertyRepository.findById(propertyId).orElseThrow(()
            //       -> new NotFoundException("Property not found"));

            // Save the image metadata in the database
            PropertyImage propertyImage = new PropertyImage();
            propertyImage.setImageFileName(imageFileName);
            propertyImage.setProperty(property);
            propertyImageRepository.save(propertyImage);

            // Save the image file name and associate it with the property
            property.addImage(propertyImage);
            propertyRepository.save(property);

            return imageFileName;
        } catch (IOException e) {
            System.out.println("Error storing property image: " + e.getMessage());
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
