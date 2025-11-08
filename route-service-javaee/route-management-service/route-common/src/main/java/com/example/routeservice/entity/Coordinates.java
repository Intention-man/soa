package com.example.routeservice.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "coordinates")
@XmlRootElement(name = "coordinates")
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int x;

    @NotNull
    private Long y;

    public Coordinates() {}

    public Coordinates(Integer x, long y) {
        this.x = x;
        this.y = y;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public int getX() { return x; }

    public void setX(int x) { this.x = x; }

    public Long getY() { return y; }

    public void setY(Long y) { this.y = y; }
}
