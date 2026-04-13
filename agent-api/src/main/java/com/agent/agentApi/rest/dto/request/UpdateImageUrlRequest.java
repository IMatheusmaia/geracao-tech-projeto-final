package com.agent.agentApi.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateImageUrlRequest(
    @NotBlank String imageUrl
) {}
