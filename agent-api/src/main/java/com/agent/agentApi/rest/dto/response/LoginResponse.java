package com.agent.agentApi.rest.dto.response;

import com.agent.agentApi.rest.auth.UserRole;

public record LoginResponse(
    String token,
    String name,
    String email,
    UserRole role
) {}
