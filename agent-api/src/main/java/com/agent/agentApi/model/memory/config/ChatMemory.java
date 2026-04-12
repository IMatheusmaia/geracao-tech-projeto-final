package com.agent.agentApi.model.memory.config;

import org.neo4j.driver.Driver;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.neo4j.Neo4jChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.neo4j.Neo4jChatMemoryRepositoryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ChatMemory {

  @Bean
  public ChatMemoryRepository chatMemoryRepository(Driver driver) {
    Neo4jChatMemoryRepositoryConfig config = Neo4jChatMemoryRepositoryConfig.builder()
      .withDriver(driver)
      .build();
    return new Neo4jChatMemoryRepository(config);
  }

  @Bean
  MessageWindowChatMemory windowChatMemory(ChatMemoryRepository chatMemoryRepository) {
    return MessageWindowChatMemory.builder()
      .maxMessages(200)
      .chatMemoryRepository(chatMemoryRepository)
      .build();
  }
  
}
