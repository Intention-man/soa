package com.example.routeservice.entity;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

@Entity
@Table(name = "routes")
@XmlRootElement(name = "route")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
        @NamedQuery(name = "Route.findAll", query = "SELECT r FROM Route r"),
        @NamedQuery(name = "Route.countAll", query = "SELECT COUNT(r) FROM Route r"),
        @NamedQuery(name = "Route.sumDistance",
                query = "SELECT SUM(r.distance), COUNT(r), MIN(r.distance), MAX(r.distance) FROM Route r"),
        @NamedQuery(name = "Route.groupByDistance",
                query = "SELECT r.distance, COUNT(r) FROM Route r GROUP BY r.distance ORDER BY r.distance"),
        @NamedQuery(name = "Route.findByDistanceGreaterThan",
                query = "SELECT r FROM Route r WHERE r.distance > :minDistance ORDER BY r.distance")
})
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement
    private Long id;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Название маршрута не может быть пустым")
    @NotNull(message = "Название маршрута не может быть null")
    @XmlElement
    private String name;

    @Embedded
    @Valid
    @NotNull(message = "Координаты не могут быть null")
    @XmlElement
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false)
    @NotNull
    @XmlElement
    private LocalDateTime creationDate;

    @Embedded
    @Valid
    @NotNull(message = "Начальная точка не может быть null")
    @XmlElement
    private FromLocation fromLocation;

    @Embedded
    @Valid
    @NotNull(message = "Конечная точка не может быть null")
    @XmlElement
    private ToLocation toLocation;

    @Column(nullable = false)
    @DecimalMin(value = "1.01", message = "Дистанция должна быть больше 1")
    @NotNull(message = "Дистанция не может быть null")
    @XmlElement
    private Double distance;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }

    // Конструкторы, геттеры и сеттеры
    public Route() {}

    public Route(String name, Coordinates coordinates, FromLocation fromLocation,
                 ToLocation toLocation, Double distance) {
        this.name = name;
        this.coordinates = coordinates;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.distance = distance;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    public FromLocation getFromLocation() { return fromLocation; }
    public void setFromLocation(FromLocation fromLocation) { this.fromLocation = fromLocation; }
    public ToLocation getToLocation() { return toLocation; }
    public void setToLocation(ToLocation toLocation) { this.toLocation = toLocation; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
}