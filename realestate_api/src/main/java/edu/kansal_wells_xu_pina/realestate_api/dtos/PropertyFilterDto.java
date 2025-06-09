package edu.kansal_wells_xu_pina.realestate_api.dtos;

public class PropertyFilterDto {
    private String zipCode;
    private Integer minSqFt;
    private Double minPrice;
    private Double maxPrice;
    private String sortBy; // "price_asc", "price_desc", "sqft_asc", "sqft_desc", "date_desc"

    // Default constructor
    public PropertyFilterDto() {}

    // Getters and Setters
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Integer getMinSqFt() {
        return minSqFt;
    }

    public void setMinSqFt(Integer minSqFt) {
        this.minSqFt = minSqFt;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
} 