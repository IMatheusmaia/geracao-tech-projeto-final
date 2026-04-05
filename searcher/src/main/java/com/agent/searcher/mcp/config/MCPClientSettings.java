package com.agent.searcher.mcp.config;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.McpClient.SyncSpec;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;

import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class MCPClientSettings {

  @Value("${spring.mcp.server.url}")
  private String serverUrl;

  @Value("${spring.mcp.auth.token}")
  private String authToken;

  @Bean
  public HttpClientSseClientTransport mcpSseTransport() {
    return HttpClientSseClientTransport.builder(serverUrl)
        .customizeRequest(requestBuilder ->
            requestBuilder.header("Authorization", "Bearer " + authToken))
        .build();
  }

  @Bean
  public McpSyncClient mcpSyncClient(HttpClientSseClientTransport transport) {
    return McpClient.sync(transport)
    .build();
  }

}
