package edu.kansal_wells_xu_pina.realestate_api.services;


import edu.kansal_wells_xu_pina.realestate_api.dtos.UpdateProfileRequest;
import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AgentService {

    User getAgentById(Long id);
    List<Property> getAgentProperties(Long agentId);
    Property getAgentPropertyById(Long agentId, Long propertyId);
    Property getCurrentProperty(Long propertyId);
    Property addNewProperty(Property newProperty, List<MultipartFile> imageFiles);
    public void updateProperty(Property propertyToUpdate, String title, Double price, String description,
                      String location, Integer size);
    String deletePropertyByPropertyId(Long propertyId, Long agentId);
}