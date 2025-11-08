package com.example.routeservice.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Singleton
@Startup
public class ConsulRegistration {

    private final ConsulClient consulClient = new ConsulClient("localhost", 8550);

    @PostConstruct
    public void registerService() {
        NewService service = new NewService();
        service.setId("route-service-1");
        service.setName("route-service");
        service.setPort(18080); // порт веб-модуля
        service.setAddress("localhost"); // IP/hostname
        consulClient.agentServiceRegister(service);
    }
}
