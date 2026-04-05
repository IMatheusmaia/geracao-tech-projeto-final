package com.agent.searcher.rest.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.agent.searcher.model.prompts.utils.DateTimeBR;
import com.fasterxml.jackson.annotation.JsonProperty;

@RedisHash("content")
public class ContentEntity {
  
  @Id private String id;
  private String title;
  private String content;

  @JsonProperty("saved_at")
  private String savedAt;

  @JsonProperty("updated_at")
  private String updatedAt;

  public ContentEntity() {}

  public ContentEntity(
    String title,
    String content
  ) {
    this.id = String.valueOf(UUID.randomUUID());
    this.title = title;
    this.content = content;
    this.savedAt = DateTimeBR.getTime();
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getSavedAt() {
    return savedAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
}
