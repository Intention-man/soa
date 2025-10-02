package com.example.routeservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

@Embeddable
@XmlRootElement(name = "toLocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class ToLocation {

    @Column(name = "to_x", nullable = false)
    @NotNull(message = "Координата X конечной точки не может быть null")
    @XmlElement
    private Long x;

    @Column(name = "to_y", nullable = false)
    @XmlElement
    private int y;

    @Column(name = "to_name", nullable = false, length = 255)
    @NotBlank(message = "Название конечной точки не может быть пустым")
    @NotNull(message = "Название конечной точки не может быть null")
    @XmlElement
    private String name;

    // Конструкторы, геттеры и сеттеры
    public ToLocation() {}

    public ToLocation(Long x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public Long getX() { return x; }
    public void setX(Long x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}