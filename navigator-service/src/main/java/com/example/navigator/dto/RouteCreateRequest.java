package com.example.navigator.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "RouteCreateRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteCreateRequest {
    @XmlElement
    private String name;

    @XmlElement
    private Coordinates coordinates;

    @XmlElement(name = "fromLocation")
    private FromLocation fromLocation;

    @XmlElement(name = "toLocation")
    private ToLocation toLocation;

    @XmlElement
    private Double distance;

    public RouteCreateRequest() {}

    public RouteCreateRequest(String name, Coordinates coordinates,
                              FromLocation fromLocation, ToLocation toLocation, Double distance) {
        this.name = name;
        this.coordinates = coordinates;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.distance = distance;
    }

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
}
