package com.example.routeservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

@Embeddable
@XmlRootElement(name = "fromLocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class FromLocation {

    @Column(name = "from_x", nullable = false)
    @NotNull(message = "Координата X начальной точки не может быть null")
    @XmlElement
    private Integer x;

    @Column(name = "from_y", nullable = false)
    @XmlElement
    private long y;

    @Column(name = "from_name", nullable = false, length = 255)
    @NotNull(message = "Название начальной точки не может быть null")
    @XmlElement
    private String name;

    public FromLocation() {}

    public FromLocation(Integer x, long y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public Integer getX() { return x; }
    public void setX(Integer x) { this.x = x; }
    public long getY() { return y; }
    public void setY(long y) { this.y = y; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}