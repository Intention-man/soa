package com.example.routeservice.dto;

import com.example.routeservice.entity.Route;
import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "RouteListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteListResponse {

    @XmlElementWrapper(name = "routes")
    @XmlElement(name = "route")
    private List<Route> routes;

    @XmlElement
    private Long totalElements;

    @XmlElement
    private Integer totalPages;

    @XmlElement
    private Integer currentPage;

    @XmlElement
    private Integer pageSize;

    // Конструкторы, геттеры и сеттеры
    public RouteListResponse() {}

    public RouteListResponse(List<Route> routes, Long totalElements,
                             Integer totalPages, Integer currentPage, Integer pageSize) {
        this.routes = routes;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    // Геттеры и сеттеры
    public List<Route> getRoutes() { return routes; }
    public void setRoutes(List<Route> routes) { this.routes = routes; }
    public Long getTotalElements() { return totalElements; }
    public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }
    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
    public Integer getCurrentPage() { return currentPage; }
    public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}