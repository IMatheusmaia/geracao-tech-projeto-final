package com.agent.agentApi.rest.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Positive;

public record DishRequest(
    String title,
    String description,
    String category,
    @Positive(message = "Preço deve ser positivo")
    BigDecimal price,
    String imageUrl,
    List<String> ingredients
) {}
