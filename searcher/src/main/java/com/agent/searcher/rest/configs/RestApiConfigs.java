package com.agent.searcher.rest.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import io.lettuce.core.ReadFrom;

@Configuration(proxyBeanMethods = false)
@EnableRedisRepositories
public class RestApiConfigs {

  @Bean
  public LettuceConnectionFactory lettuceConnection() {
    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
      .readFrom(ReadFrom.REPLICA_PREFERRED)
      .build();

    return new LettuceConnectionFactory(
      new RedisStandaloneConfiguration("localhost", 6379),
      clientConfig
    );
  }

  @Bean
  public RedisTemplate<?, ?> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {

    RedisTemplate<byte[], byte[]> template = new RedisTemplate<byte[], byte[]>();
    template.setConnectionFactory(redisConnectionFactory);
    return template;
  }
  
}
