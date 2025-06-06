/* package edu.kansal_wells_xu_pina.realestate_api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PropertyImageService {

    private String uploadDir;


    public String saveImage(MultipartFile file) throws IOException {

    /* Image upload functionality - not yet implemented
    public String saveImage(MuitiPartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IOException("File is empty, file store failed.");
        }

        String projectRoot = System.getProperty("user.dir");
        Path uploadPath = Paths.get(projectRoot, uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        file.transferTo(filePath.toFile());
    }

}
*/