package com.example.routeservice.config;


import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;

@DataSourceDefinition(
        name = "java:app/jdbc/routesDS",
        className = "org.postgresql.xa.PGXADataSource",
        user = "${ENV=DB_USER:s367044}",
        password = "${ENV=DB_PASSWORD:rkemWfU26OYiwbkD}",
        databaseName = "${ENV=DB_NAME:studs}",
        serverName = "${ENV=DB_HOST:localhost}",
        portNumber = 25432
)
@ApplicationScoped
public class DatabaseConfig {
}