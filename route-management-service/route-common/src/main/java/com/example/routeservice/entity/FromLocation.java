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
@Table(name = "from_locations")
@XmlRootElement(name = "fromLocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class FromLocation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private Integer x;
    private long y;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Integer getX() { return x; }

    public void setX(Integer x) { this.x = x; }

    public long getY() { return y; }

    public void setY(long y) { this.y = y; }
}
