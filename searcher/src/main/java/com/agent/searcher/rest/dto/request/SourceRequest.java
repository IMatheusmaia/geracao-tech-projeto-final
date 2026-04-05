package com.agent.searcher.rest.dto.request;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;

public record SourceRequest(
  @NotBlank(message = "name é obrigatório")
  String name,

  @NotBlank(message = "baseUrl é obrigatório")
  @URL(message = "baseUrl deve ser uma URL válida")
  String baseUrl
) {}
