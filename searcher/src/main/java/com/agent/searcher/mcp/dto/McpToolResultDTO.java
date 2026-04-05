package com.agent.searcher.mcp.dto;

import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonProperty;

public record McpToolResultDTO(
    List<New> news,

    @JsonProperty("url_source")
    String urlSource
) {
  public record New(
    String title,
    String content,
    
    @JsonProperty("pub_date")
    String pubDate,
    
    @JsonProperty("name_source")
    Optional<String> nameSource,
    
    @JsonProperty("url_source")
    String urlSource
  ) {
  }
}
