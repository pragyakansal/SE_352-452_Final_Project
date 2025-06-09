package edu.kansal_wells_xu_pina.realestate_api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface PropertyImageService {

    String storePropertyImage(Long propertyId, MultipartFile file) throws IOException;
    void deletePropertyImage(Long propertyId, Long imageId) throws IOException;
}