package com.example.navigator.dto;

import javax.xml.bind.annotation.XmlAccessType; // 🔑 Изменено
import javax.xml.bind.annotation.XmlAccessorType; // 🔑 Изменено
import javax.xml.bind.annotation.XmlElement; // 🔑 Изменено
import javax.xml.bind.annotation.XmlRootElement; // 🔑 Изменено
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "RouteCreateRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteCreateRequest {
    @XmlElement
    private String name;

    @XmlElement
    private Coordinates coordinates;

    @XmlElement(name = "fromLocation")
    private FromLocation fromLocation;

    @XmlElement(name = "toLocation")
    private ToLocation toLocation;

    @XmlElement
    private Double distance;
}