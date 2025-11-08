package com.example.routeservice.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "routes")
@XmlRootElement(name = "route")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
        @NamedQuery(name = "Route.sumDistance", query = "SELECT SUM(r.distance), COUNT(r), MIN(r.distance), MAX(r.distance) FROM Route r"),
        @NamedQuery(name = "Route.groupByDistance", query = "SELECT r.distance, COUNT(r) FROM Route r GROUP BY r.distance"),
        @NamedQuery(name = "Route.findByDistanceGreaterThan", query = "SELECT r FROM Route r WHERE r.distance > :minDistance")
})
public class Route implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @ManyToOne
    @JoinColumn(name = "from_location_id", nullable = false)
    private FromLocation fromLocation;

    @ManyToOne
    @JoinColumn(name = "to_location_id", nullable = false)
    private ToLocation toLocation;

    @NotNull
    private Double distance;

    @XmlTransient
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @XmlElement(name = "creationDate")
    public String getCreationDateAsString() {
        return creationDate != null ? creationDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }

    public void setCreationDateAsString(String date) {
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Coordinates getCoordinates() { return coordinates; }

    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public FromLocation getFromLocation() { return fromLocation; }

    public void setFromLocation(FromLocation fromLocation) { this.fromLocation = fromLocation; }

    public ToLocation getToLocation() { return toLocation; }

    public void setToLocation(ToLocation toLocation) { this.toLocation = toLocation; }

    public Double getDistance() { return distance; }

    public void setDistance(Double distance) { this.distance = distance; }

    public LocalDateTime getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
}
