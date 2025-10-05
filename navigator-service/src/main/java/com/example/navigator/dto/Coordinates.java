package com.example.navigator.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "coordinates")
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates {
    private Integer x;
    private Long y;

    public Coordinates() {}
    public Coordinates(Integer x, Long y) { this.x = x; this.y = y; }

    public Integer getX() { return x; }
    public void setX(Integer x) { this.x = x; }
    public Long getY() { return y; }
    public void setY(Long y) { this.y = y; }
}
