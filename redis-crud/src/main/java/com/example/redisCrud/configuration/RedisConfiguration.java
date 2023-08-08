package com.example.redisCrud.configuration;

import com.example.redisCrud.model.Account;
import com.example.redisCrud.model.Food;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

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
    public RedisTemplate<String, Food> redisFoodTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Food> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        Jackson2JsonRedisSerializer<Food> jacksonFoodSerializer = new Jackson2JsonRedisSerializer<>(Food.class);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(jacksonFoodSerializer);
        return template;
    }

    @Bean
    public RedisTemplate<String, Account> redisAccountTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Account> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Account> jacksonAccountSerializer = new Jackson2JsonRedisSerializer<>(Account.class);
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(jacksonAccountSerializer);

        return template;
    }
}
