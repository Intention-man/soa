package com.example.navigator.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "route")
@XmlAccessorType(XmlAccessType.FIELD)
public class Route {
    @XmlElement
    private Long id;

    @XmlElement
    private String name;

    @XmlElement
    private Coordinates coordinates;

    @XmlElement
    private String creationDate;

    @XmlElement(name = "fromLocation")
    private FromLocation fromLocation;

    @XmlElement(name = "toLocation")
    private ToLocation toLocation;

    @XmlElement
    private Double distance;

    public Route() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
    public FromLocation getFromLocation() { return fromLocation; }
    public void setFromLocation(FromLocation fromLocation) { this.fromLocation = fromLocation; }
    public ToLocation getToLocation() { return toLocation; }
    public void setToLocation(ToLocation toLocation) { this.toLocation = toLocation; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
}
