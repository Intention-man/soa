package com.example.routeservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "coordinates")
@XmlRootElement(name = "coordinates")
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int x;

    @NotNull
    private Long y;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public int getX() { return x; }

    public void setX(int x) { this.x = x; }

    public Long getY() { return y; }

    public void setY(Long y) { this.y = y; }
}
