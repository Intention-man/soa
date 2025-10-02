package com.example.routeservice.dto;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DistanceGroupResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DistanceGroupResponse {

    @XmlElementWrapper(name = "groups")
    @XmlElement(name = "group")
    private List<DistanceGroup> groups;

    @XmlElement
    private Integer totalGroups;

    @XmlElement
    private Integer totalRoutes;

    // Вложенный класс для группы
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DistanceGroup {
        @XmlElement
        private Double distance;

        @XmlElement
        private Integer count;

        @XmlElement
        private Double percentage;

        // Конструкторы, геттеры и сеттеры
        public DistanceGroup() {
        }

        public DistanceGroup(Double distance, Integer count, Double percentage) {
            this.distance = distance;
            this.count = count;
            this.percentage = percentage;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }

    // Конструкторы, геттеры и сеттеры
    public DistanceGroupResponse() {
    }

    public DistanceGroupResponse(List<DistanceGroup> groups, Integer totalGroups, Integer totalRoutes) {
        this.groups = groups;
        this.totalGroups = totalGroups;
        this.totalRoutes = totalRoutes;
    }

    public List<DistanceGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<DistanceGroup> groups) {
        this.groups = groups;
    }

    public Integer getTotalGroups() {
        return totalGroups;
    }

    public void setTotalGroups(Integer totalGroups) {
        this.totalGroups = totalGroups;
    }

    public Integer getTotalRoutes() {
        return totalRoutes;
    }

    public void setTotalRoutes(Integer totalRoutes) {
        this.totalRoutes = totalRoutes;
    }
}