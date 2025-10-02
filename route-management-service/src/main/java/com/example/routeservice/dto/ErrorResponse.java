package com.example.routeservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ErrorResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse {

    @XmlElement
    private Integer code;

    @XmlElement
    private String message;

    @XmlElement
    private LocalDateTime timestamp;

    @XmlElement
    private String path;

    @XmlElementWrapper(name = "details")
    @XmlElement(name = "detail")
    private List<String> details;

    // Конструкторы, геттеры и сеттеры
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(Integer code, String message, String path) {
        this();
        this.code = code;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(Integer code, String message, String path, List<String> details) {
        this(code, message, path);
        this.details = details;
    }

    // Геттеры и сеттеры
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}