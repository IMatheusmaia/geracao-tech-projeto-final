package com.agent.searcher.rest.entity;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import com.fasterxml.jackson.annotation.JsonProperty;

@RedisHash("source")
public class SourceEntity {

  @Id private String id;

  private String name;

  @JsonProperty("base_url")
  private String baseUrl;

  public SourceEntity() {}

  public SourceEntity(String name, String baseUrl) {
    this.id = String.valueOf(UUID.randomUUID());
    this.name = name;
    this.baseUrl = baseUrl;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }
  
}
