package com.example.redisCrud.integration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DockerConfigurationIntegrationTest {
    public static GenericContainer<?> redis;

    static {
        redis = new FixedHostPortGenericContainer("redis:5.0.3-alpine")
                .withFixedExposedPort(6379, 6379);
        redis.start();
    }

}
