package edu.kansal_wells_xu_pina.realestate_api.dtos;

public class ErrorDataHolderDto {
    private String error;
    private String timestamp;
    private Integer status;
    private String path;
    private String exception;
    private String errors;

    public ErrorDataHolderDto(String error, String timestamp, String path, String exception, String errors) {
        this.error = error;
        this.timestamp = timestamp;
        this.path = path;
        this.exception = exception;
        this.errors = errors;
    }

    // Getters
    public String getError() {
        return this.error;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getPath() {
        return this.path;
    }

    public String getException() {
        return this.exception;
    }

    public String getErrors() {
        return this.errors;
    }

    // Setters
    public void setError(String error) {
        this.error = error;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
} 