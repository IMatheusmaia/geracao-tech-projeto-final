package com.agent.searcher.rest.dto.response;

public record ContentResponse(
  String id,
  String title,
  String content,
  String savedAt,
  String updatedAt
) {}
