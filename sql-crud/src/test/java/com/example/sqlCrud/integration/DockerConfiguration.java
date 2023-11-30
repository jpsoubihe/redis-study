package com.example.sqlCrud.integration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DockerConfiguration {
    public static GenericContainer<?> mariadb;

    static {
        mariadb = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"));
        mariadb.start();
    }
}
