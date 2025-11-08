package com.example.navigator.dto;

import javax.xml.bind.annotation.XmlAccessType; // 🔑 Изменено
import javax.xml.bind.annotation.XmlAccessorType; // 🔑 Изменено
import javax.xml.bind.annotation.XmlRootElement; // 🔑 Изменено
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "toLocation")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToLocation {
    private Long x;
    private int y;
    private String name;
}