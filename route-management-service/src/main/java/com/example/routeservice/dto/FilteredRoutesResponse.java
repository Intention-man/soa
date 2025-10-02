package com.example.routeservice.dto;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.example.routeservice.entity.Route;

@XmlRootElement(name = "FilteredRoutesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilteredRoutesResponse {

    @XmlElementWrapper(name = "routes")
    @XmlElement(name = "route")
    private List<Route> routes;

    @XmlElement
    private Integer count;

    @XmlElement
    private Double minDistance;

    @XmlElement
    private Double maxDistance;

    @XmlElement
    private Double averageDistance;

    // Конструкторы, геттеры и сеттеры
    public FilteredRoutesResponse() {
    }

    public FilteredRoutesResponse(List<Route> routes, Integer count,
                                  Double minDistance, Double maxDistance, Double averageDistance) {
        this.routes = routes;
        this.count = count;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.averageDistance = averageDistance;
    }

    // Геттеры и сеттеры
    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(Double minDistance) {
        this.minDistance = minDistance;
    }

    public Double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Double getAverageDistance() {
        return averageDistance;
    }

    public void setAverageDistance(Double averageDistance) {
        this.averageDistance = averageDistance;
    }
}