package edu.kansal_wells_xu_pina.realestate_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "property_images")
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageFileName;

    @ManyToOne
    @JoinColumn(name = "property_id")
    @JsonIgnore
    private Property property;

    public PropertyImage() { }

    public PropertyImage(String imageFileName, Property property) {
        this.imageFileName = imageFileName;
        this.property = property;
    }

    public Long getId() {
        return id;
    }

    public String getImageFileName() {
        return imageFileName;
    }
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }
}