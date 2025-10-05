package com.example.navigator.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
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

    public RouteListResponse() {}

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
