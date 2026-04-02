package com.agent.searcher.mcp.client;

import java.net.http.HttpRequest;
import java.util.List;

import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.spec.McpSchema;
import reactor.core.publisher.Mono;
import io.modelcontextprotocol.client.McpClient;

@Configuration(proxyBeanMethods = false)
public class MCPClientConfig {

  @Value("${spring.config.mcp.server.url}")
  private String serverUrl;

  @Value("${mcp.auth.token}")
  private String authToken;

  @Bean
  public HttpClientSseClientTransport mcpSseTransport() {
    return HttpClientSseClientTransport.builder(this.serverUrl)
        .customizeRequest(requestBuilder ->
            requestBuilder.header("Authorization", "Bearer " + authToken))
        .build();
  }
}
