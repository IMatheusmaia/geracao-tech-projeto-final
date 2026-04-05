package com.agent.searcher.model.tools.params;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ToolMCPParam(
  Type type,
  List<Step> steps,
  StructuredOutput structuredOutput
) {
  public record Type(
      String name,
      String description
  ) {
  }
  public record Step(
    String step,
    String description
  ) {
  }

  public record StructuredOutput(
    String title,
    String content,
    @JsonProperty("pub_date")
    String pubDate,
    @JsonProperty("name_source")
    String nameSource,
    @JsonProperty("url_source")
    String url_source
  ) {
  }
}
