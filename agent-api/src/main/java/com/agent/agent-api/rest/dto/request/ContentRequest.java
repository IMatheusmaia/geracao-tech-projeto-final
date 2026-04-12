package com.agent.searcher.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ContentRequest(
  @NotBlank(message = "title é obrigatório")
  String title,

  @NotBlank(message = "content é obrigatório")
  String content
) {}
