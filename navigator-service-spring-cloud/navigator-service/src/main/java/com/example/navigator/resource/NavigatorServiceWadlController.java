package com.example.navigator.resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/navigator-service")
public class NavigatorServiceWadlController {
    @GetMapping(value = "/application.wadl", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getWadl() {
        String wadlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><application xmlns=\"http://wadl.dev.java.net/2009/02\"><resources base=\"https://localhost:38090/navigator\"><resource path=\"/routes/{idFrom}/{idTo}/{orderBy}\"><param name=\"idFrom\" type=\"xs:long\" style=\"template\"/><param name=\"idTo\" type=\"xs:long\" style=\"template\"/><param name=\"orderBy\" type=\"xs:string\" style=\"template\"/><method name=\"GET\"/></resource><resource path=\"/route/add/{idFrom}/{idTo}/{distance}\"><param name=\"idFrom\" type=\"xs:long\" style=\"template\"/><param name=\"idTo\" type=\"xs:long\" style=\"template\"/><param name=\"distance\" type=\"xs:double\" style=\"template\"/><method name=\"POST\"/></resource></resources></application>";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(wadlContent);
    }
}

