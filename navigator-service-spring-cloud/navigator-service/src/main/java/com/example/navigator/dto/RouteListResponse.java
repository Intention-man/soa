package com.example.navigator.dto;

import javax.xml.bind.annotation.XmlAccessType; // 🔑 Изменено
import javax.xml.bind.annotation.XmlAccessorType; // 🔑 Изменено
import javax.xml.bind.annotation.XmlElement; // 🔑 Изменено
import javax.xml.bind.annotation.XmlElementWrapper; // 🔑 Изменено
import javax.xml.bind.annotation.XmlRootElement; // 🔑 Изменено
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "RouteListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class RouteListResponse {

    @XmlElementWrapper(name = "routes")
    @XmlElement(name = "route")
    private List<Route> routes;

    @XmlElement
    private Long totalElements;

    @XmlElement
    private Integer totalPages;

    @XmlElement
    private Integer currentPage;

    @XmlElement
    private Integer pageSize;
}