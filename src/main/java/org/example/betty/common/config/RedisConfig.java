package org.example.betty.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host1}")
    private String host1;

    @Value("${spring.data.redis.host2}")
    private String host2;

    @Value("${spring.data.redis.host3}")
    private String host3;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.port.redis1}")
    private int port1;

    @Value("${spring.data.redis.port.redis2}")
    private int port2;

    @Value("${spring.data.redis.port.redis3}")
    private int port3;

    @Bean
    public RedisConnectionFactory redisConnectionFactoryRedis1() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host1, port1);
        config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactoryRedis2() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host2, port2);
        config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactoryRedis3() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host3, port3);
        config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        return createRedisTemplate(redisConnectionFactoryRedis1());
    }

    @Bean(name = "redisTemplate2")
    public RedisTemplate<String, Object> redisTemplate2() {
        return createRedisTemplate(redisConnectionFactoryRedis2());
    }

    @Bean(name = "redisTemplate3")
    public RedisTemplate<String, Object> redisTemplate3() {
        return createRedisTemplate(redisConnectionFactoryRedis3());
    }

    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(factory);
        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        tpl.setHashKeySerializer(new StringRedisSerializer());
        tpl.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        tpl.afterPropertiesSet();
        return tpl;
    }

}
