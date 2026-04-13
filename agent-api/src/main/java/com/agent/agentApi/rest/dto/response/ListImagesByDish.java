package com.agent.agentApi.rest.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ListImagesByDish(
  List<Image> images
) {
  public record Image(
    @JsonProperty("image_url")
    String imageUrl
  ) {}
}
