package com.example.routeservice.dto;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "DistanceSumResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DistanceSumResponse {

    @XmlElement
    private Double totalSum;

    @XmlElement
    private Integer routeCount;

    @XmlElement
    private Double averageDistance;

    @XmlElement
    private Double minDistance;

    @XmlElement
    private Double maxDistance;

    // Конструкторы, геттеры и сеттеры
    public DistanceSumResponse() {}

    public DistanceSumResponse(Double totalSum, Integer routeCount,
                               Double averageDistance, Double minDistance, Double maxDistance) {
        this.totalSum = totalSum;
        this.routeCount = routeCount;
        this.averageDistance = averageDistance;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    // Геттеры и сеттеры
    public Double getTotalSum() { return totalSum; }
    public void setTotalSum(Double totalSum) { this.totalSum = totalSum; }
    public Integer getRouteCount() { return routeCount; }
    public void setRouteCount(Integer routeCount) { this.routeCount = routeCount; }
    public Double getAverageDistance() { return averageDistance; }
    public void setAverageDistance(Double averageDistance) { this.averageDistance = averageDistance; }
    public Double getMinDistance() { return minDistance; }
    public void setMinDistance(Double minDistance) { this.minDistance = minDistance; }
    public Double getMaxDistance() { return maxDistance; }
    public void setMaxDistance(Double maxDistance) { this.maxDistance = maxDistance; }
}