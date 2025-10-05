package com.example.navigator.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "toLocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class ToLocation {
    private Long x;
    private int y;
    private String name;

    public ToLocation() {}
    public ToLocation(Long x, int y, String name) { this.x = x; this.y = y; this.name = name; }

    public Long getX() { return x; }
    public void setX(Long x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
