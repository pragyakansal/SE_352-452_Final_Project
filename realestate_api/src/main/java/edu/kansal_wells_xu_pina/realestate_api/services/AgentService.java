package edu.kansal_wells_xu_pina.realestate_api.services;


import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.NotFoundException;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;

/*
@Service
public interface AgentService {

    List<Property> getAllProperties(Long agentId);
    Property addProperty(Long agentId, Property property);
    Property editProperty(Property property);
    Agent getAgent();




    /*

    private final TreeMap<String, Agent> agentMap = new TreeMap<>();

    public AgentService() {

    }

    public Agent getCurrentAgent() {
       //TO-DO: Need to implement logic to retrieve the current agent
        return null; // Placeholder for current agent retrieval logic
    }

    // TO-DO: Implement logic to get all properties for the current agent
    // Check to see if this should be in a different service
    public Agent getAllProperties() {
        // TO-DO: Need to implement logic to retrieve all properties for the current agent
        return null;
    }


    // TO-DO: Implement logic to retrieve an agent by ID
    public Agent getAgent(Long id) {
        if (id == null || id <= 0)
            throw new NotFoundException("Agent Name Cannot Be Null or Empty");
        Agent agent = agentMap.get(id);
        if (agent == null)
            throw new NotFoundException("Agent Not Found");
        return agent;
    }

    // TO-DO: Implement logic to add a property to an agent
    // Does this need to be in a differnt class or can it be in this service?
    public void addProperty(String agentName, Property property) {

    }

    // TO-DO: Implement logic to save a property for an agent
    // Does this need to be in a different class or can it be in this service?
    public  Agent saveProperty(Property property) {
        return null;
    }

} */
