package com.example.routeservice.filter;

import java.util.Map;

public class RouteFilter {
    private Integer page = 0;
    private Integer size = 20;
    private String[] sort;
    private Map<String, String> filterParams;

    public RouteFilter() {}

    public RouteFilter(Integer page, Integer size, String[] sort, Map<String, String> filterParams) {
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.filterParams = filterParams;
    }

    // Геттеры и сеттеры
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String[] getSort() { return sort; }
    public void setSort(String[] sort) { this.sort = sort; }
    public Map<String, String> getFilterParams() { return filterParams; }
    public void setFilterParams(Map<String, String> filterParams) { this.filterParams = filterParams; }
}