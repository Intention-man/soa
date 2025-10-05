package com.example.navigator.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fromLocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class FromLocation {
    private Integer x;
    private long y;
    private String name;

    public FromLocation() {}
    public FromLocation(Integer x, long y, String name) { this.x = x; this.y = y; this.name = name; }

    public Integer getX() { return x; }
    public void setX(Integer x) { this.x = x; }
    public long getY() { return y; }
    public void setY(long y) { this.y = y; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
