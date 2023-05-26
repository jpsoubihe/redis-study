package com.example.redisStudy.configuration;

import com.example.redisStudy.model.Food;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {

    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port:6379}")
    private Integer redisPort;
    @Value("${redis.password:}")
    private String redisPassword;

    private RedisConnectionFactory newConnectionFactory(String host, Integer port, String password) {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
        connectionFactory.setHostName(host);
        connectionFactory.setPort(port);
        if (StringUtils.isNotBlank(password)) {
            connectionFactory.setPassword(password);
        }
        return connectionFactory;
    }

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        return newConnectionFactory(redisHost, redisPort, redisPassword);
    }

    @Bean
    public RedisTemplate<Long, Food> redisFoodTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, Food> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // Add some specific configuration here. Key serializers, etc.
        return template;
    }

    @Bean
    public RedisTemplate<Long, Food> redisAccountTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, Food> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // Add some specific configuration here. Key serializers, etc.
        return template;
    }
}
