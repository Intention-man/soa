package com.example.routeservice.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.example.routeservice.entity.Coordinates;
import com.example.routeservice.entity.FromLocation;
import com.example.routeservice.entity.ToLocation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@XmlRootElement(name = "RouteCreateRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteCreateRequest {

    @NotBlank(message = "Название маршрута не может быть пустым")
    @Size(max = 255, message = "Название маршрута не может превышать 255 символов")
    @XmlElement
    private String name;

    @Valid
    @NotNull(message = "Координаты не могут быть null")
    @XmlElement
    private Coordinates coordinates;

    @Valid
    @NotNull(message = "Начальная точка не может быть null")
    @XmlElement
    private FromLocation fromLocation;

    @Valid
    @NotNull(message = "Конечная точка не может быть null")
    @XmlElement
    private ToLocation toLocation;

    @DecimalMin(value = "1.01", message = "Дистанция должна быть больше 1")
    @NotNull(message = "Дистанция не может быть null")
    @XmlElement
    private Double distance;

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public FromLocation getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(FromLocation fromLocation) {
        this.fromLocation = fromLocation;
    }

    public ToLocation getToLocation() {
        return toLocation;
    }

    public void setToLocation(ToLocation toLocation) {
        this.toLocation = toLocation;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}