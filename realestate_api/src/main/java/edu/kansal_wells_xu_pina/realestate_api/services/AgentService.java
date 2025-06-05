package edu.kansal_wells_xu_pina.realestate_api.services;


import edu.kansal_wells_xu_pina.realestate_api.dtos.UpdateProfileRequest;
import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AgentService {

    User getAgentById(Long id);
    /* Redundant profile methods - now using common profile management
    UpdateProfileRequest getAgentDtoById(Long id);
    User editAgentProfile(Long AgentId, UpdateProfileRequest request);
    */
    List<Property> getAgentProperties(Long agentId);
    Property getAgentPropertyById(Long agentId, Long propertyId);
    Property addProperty(Long agentId, Property property);
    Property EditProperty(Long agentId, Long propertyId, Property property);
    // void DeleteProperty(Long agentId, Long propertyId);

}
